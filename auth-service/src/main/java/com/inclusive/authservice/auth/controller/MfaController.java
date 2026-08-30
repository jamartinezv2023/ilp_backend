package com.inclusive.authservice.auth.controller;

import com.inclusive.authservice.auth.dto.mfa.MfaSetupRequest;
import com.inclusive.authservice.auth.dto.mfa.MfaSetupResponse;
import com.inclusive.authservice.auth.dto.mfa.MfaVerifyRequest;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.security.mfa.MfaService;
import com.inclusive.authservice.tenant.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    @PostMapping("/auth/mfa/setup")
    public ResponseEntity<MfaSetupResponse> setup(
            @AuthenticationPrincipal Jwt principal,
            @Valid @RequestBody MfaSetupRequest request
    ) {
        UserAccount user = requireOwnAccount(principal, request.email());
        // Re-enrollment/replacement requires a separate, re-authenticated flow.
        // The row lock serializes competing setup requests for this account.
        if (user.isMfaEnabled() || user.hasMfaSecret()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MFA already configured or pending");
        }
        String secret = mfaService.generateSecret();
        if (secret == null || secret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MFA setup unavailable");
        }
        user.prepareMfaEnrollment(secret);
        userAccountRepository.save(user);
        String uri = mfaService.buildQrProvisioningUri(user.getEmail(), secret);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-store")
                .body(new MfaSetupResponse(secret, uri));
    }

    @Transactional
    @PostMapping("/auth/mfa/verify")
    public ResponseEntity<Boolean> verify(
            @AuthenticationPrincipal Jwt principal,
            @Valid @RequestBody MfaVerifyRequest request
    ) {
        UserAccount user = requireOwnAccount(principal, request.email());
        if (user.isMfaEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MFA already enabled");
        }
        Integer code = request.code();
        if (code == null || code < 0 || code > 999999) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid MFA code format");
        }
        boolean valid = user.hasMfaSecret()
                && mfaService.verifyCode(user.getMfaSecret(), code);
        if (valid) {
            user.enableMfa();
            userAccountRepository.save(user);
        }
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(valid);
    }

    private UserAccount requireOwnAccount(Jwt principal, String requestedEmail) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        UUID subject;
        UUID tokenTenant;
        try {
            subject = UUID.fromString(principal.getSubject());
            tokenTenant = UUID.fromString(principal.getClaimAsString("tenantId"));
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid identity claims");
        }
        if (!tokenTenant.equals(TenantContext.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tenant mismatch");
        }
        UserAccount user = userAccountRepository.findForMfaEnrollment(subject, tokenTenant)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Account unavailable"));
        if (!user.isEnabled() || !subject.equals(user.getId())
                || !tokenTenant.equals(user.getTenantId())
                || !user.getEmail().equals(requestedEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account mismatch or unavailable");
        }
        return user;
    }
}
