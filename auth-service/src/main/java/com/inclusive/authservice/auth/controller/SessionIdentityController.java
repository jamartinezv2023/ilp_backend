package com.inclusive.authservice.auth.controller;

import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/** Online identity verification; this response does not authorize an assessment submission. */
@RestController
@RequiredArgsConstructor
public class SessionIdentityController {
    private final UserAccountRepository accounts;

    public record SessionIdentity(UUID userId, UUID tenantId) {}

    @GetMapping("/auth/session-identity")
    public ResponseEntity<SessionIdentity> identity(@AuthenticationPrincipal Jwt principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        UUID userId;
        UUID tenantId;
        try {
            String subject = principal.getSubject();
            String institution = principal.getClaimAsString("tenantId");
            userId = UUID.fromString(subject);
            tenantId = UUID.fromString(institution);
            if (!userId.toString().equalsIgnoreCase(subject) || !tenantId.toString().equalsIgnoreCase(institution)) {
                throw new IllegalArgumentException("Noncanonical identity");
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid identity claims");
        }
        if (!tenantId.equals(TenantContext.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tenant mismatch");
        }
        var account = accounts.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Account unavailable"));
        if (!account.isEnabled() || !userId.equals(account.getId()) || !tenantId.equals(account.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account unavailable");
        }
        return ResponseEntity.ok().header("Cache-Control", "no-store")
                .body(new SessionIdentity(userId, tenantId));
    }
}
