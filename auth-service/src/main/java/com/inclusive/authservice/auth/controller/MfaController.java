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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;
    private final UserAccountRepository userAccountRepository;

    @PostMapping("/setup")
    public MfaSetupResponse setup(@Valid @RequestBody MfaSetupRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        UserAccount user = userAccountRepository
                .findByEmailAndTenantId(request.email(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String secret = mfaService.generateSecret();

        user.prepareMfaEnrollment(secret);
        userAccountRepository.save(user);

        return new MfaSetupResponse(
                secret,
                mfaService.buildQrProvisioningUri(user.getEmail(), secret)
        );
    }

    @PostMapping("/verify")
    public boolean verify(@Valid @RequestBody MfaVerifyRequest request) {
        UUID tenantId = TenantContext.getTenantId();

        UserAccount user = userAccountRepository
                .findByEmailAndTenantId(request.email(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.hasMfaSecret()) {
            return false;
        }

        boolean valid = mfaService.verifyCode(
                user.getMfaSecret(),
                request.code()
        );

        if (valid) {
            user.enableMfa();
            userAccountRepository.save(user);
        }

        return valid;
    }
}
