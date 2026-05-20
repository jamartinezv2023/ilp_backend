package com.inclusive.authservice.security.mfa;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class MfaServiceImpl implements MfaService {

    private static final String ISSUER = "ILP Backend Platform 2026";

    private final GoogleAuthenticator googleAuthenticator =
            new GoogleAuthenticator();

    @Override
    public String generateSecret() {
        GoogleAuthenticatorKey key =
                googleAuthenticator.createCredentials();

        return key.getKey();
    }

    @Override
    public boolean verifyCode(String secret, int code) {
        return googleAuthenticator.authorize(secret, code);
    }

    @Override
    public String buildQrProvisioningUri(String email, String secret) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                urlEncode(ISSUER),
                urlEncode(email),
                urlEncode(secret),
                urlEncode(ISSUER)
        );
    }

    private String urlEncode(String value) {
        return java.net.URLEncoder.encode(
                value,
                java.nio.charset.StandardCharsets.UTF_8
        );
    }
}
