package com.inclusive.authservice.security.mfa;

public interface MfaService {

    String generateSecret();

    boolean verifyCode(String secret, int code);

    String buildQrProvisioningUri(String email, String secret);
}
