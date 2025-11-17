package com.inclusive.authservice.service.impl;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MfaServiceImpl {

    private final Map<String, String> userSecrets = new HashMap<>();

    public Map<String, String> enableTOTP(String username) {
        DefaultSecretGenerator generator = new DefaultSecretGenerator();
        String secret = generator.generate();
        userSecrets.put(username, secret);

        QrData data = new QrData.Builder()
                .label("Inclusive Learning Platform (" + username + ")")
                .secret(secret)
                .issuer("InclusiveLearning")
                .build();

        QrGenerator qrGenerator = new ZxingPngQrGenerator();
        byte[] imageData = qrGenerator.generate(data);
        String qrCodeImage = Base64.getEncoder().encodeToString(imageData);

        Map<String, String> response = new HashMap<>();
        response.put("secret", secret);
        response.put("qrImageBase64", "data:image/png;base64," + qrCodeImage);
        response.put("otpauth_url", data.getUri());
        return response;
    }

    public boolean verifyCode(String username, String code) {
        String secret = userSecrets.get(username);
        if (secret == null) return false;
        CodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new dev.samstevens.totp.time.SystemTimeProvider());
        return verifier.isValidCode(secret, code);
    }

    public Map<String, String> rotateSecret(String username) {
        disableTOTP(username);
        return enableTOTP(username);
    }

    public void disableTOTP(String username) {
        userSecrets.remove(username);
    }
}




