package com.inclusive.authservice.auth.controller;

import com.inclusive.authservice.auth.dto.mfa.MfaSetupRequest;
import com.inclusive.authservice.auth.dto.mfa.MfaSetupResponse;
import com.inclusive.authservice.auth.dto.mfa.MfaVerifyRequest;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.security.mfa.MfaService;
import com.inclusive.authservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;
    private final UserAccountRepository userAccountRepository;

    @PostMapping("/auth/mfa/setup")
    public ResponseEntity<MfaSetupResponse> setup(
            @RequestBody MfaSetupRequest request
    ) {
        UUID tenantId = TenantContext.getTenantId();

        UserAccount user = userAccountRepository
                .findByEmailAndTenantId(request.email(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String secret = mfaService.generateSecret();
        user.prepareMfaEnrollment(secret);
        userAccountRepository.save(user);

        String provisioningUri = mfaService.buildQrProvisioningUri(
                request.email(),
                secret
        );

        return ResponseEntity.ok(
                new MfaSetupResponse(secret, provisioningUri)
        );
    }

    @PostMapping("/auth/mfa/verify")
    public ResponseEntity<Boolean> verify(
            @RequestBody MfaVerifyRequest request
    ) {
        UUID tenantId = TenantContext.getTenantId();

        UserAccount user = userAccountRepository
                .findByEmailAndTenantId(request.email(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean valid = user.hasMfaSecret()
                && mfaService.verifyCode(user.getMfaSecret(), request.code());

        if (valid) {
            user.enableMfa();
            userAccountRepository.save(user);
        }

        return ResponseEntity.ok(valid);
    }
}
