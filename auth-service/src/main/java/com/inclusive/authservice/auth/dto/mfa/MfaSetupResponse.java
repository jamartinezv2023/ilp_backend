package com.inclusive.authservice.auth.dto.mfa;

public record MfaSetupResponse(
        String secret,
        String qrProvisioningUri
) {
}
