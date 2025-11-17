package com.inclusive.authservice.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;

public final class TotpUtil {
    private TotpUtil() {}

    public static String generateSecretBase32(int bytes) {
        byte[] buf = new byte[bytes];
        new SecureRandom().nextBytes(buf);
        return Base32.encode(buf);
    }

    public static String buildOtpAuthUri(String issuer, String label, String secretBase32, int digits, int period) {
        // otpauth://totp/ISSUER:LABEL?secret=...&issuer=...&algorithm=SHA1&period=30&digits=6
        String account = url(issuer) + ":" + url(label);
        String query   = "secret=" + url(secretBase32) +
                "&issuer=" + url(issuer) +
                "&algorithm=SHA1&period=" + period + "&digits=" + digits;
        return "otpauth://totp/" + account + "?" + query;
    }

    public static String qrFromOtpAuth(String otpauthUri) {
        return "https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=" + url(otpauthUri);
    }

    public static boolean verifyCode(String secretBase32, String code, int digits, int period, int window) {
        long now = Instant.now().getEpochSecond();
        long counter = now / period;
        for (int i = -window; i <= window; i++) {
            String candidate = totpAt(secretBase32, counter + i, digits);
            if (constantTimeEquals(candidate, code)) return true;
        }
        return false;
    }

    public static String totpNow(String secretBase32, int digits, int period) {
        long now = Instant.now().getEpochSecond();
        long counter = now / period;
        return totpAt(secretBase32, counter, digits);
    }

    private static String totpAt(String secretBase32, long counter, int digits) {
        byte[] key = Base32.decode(secretBase32);
        byte[] msg = ByteBuffer.allocate(8).putLong(counter).array();
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] h = mac.doFinal(msg);
            int offset = h[h.length - 1] & 0x0F;
            int binCode = ((h[offset] & 0x7f) << 24) |
                          ((h[offset + 1] & 0xff) << 16) |
                          ((h[offset + 2] & 0xff) << 8) |
                          (h[offset + 3] & 0xff);
            int otp = binCode % (int)Math.pow(10, digits);
            return String.format("%0" + digits + "d", otp);
        } catch (Exception e) {
            throw new IllegalStateException("TOTP error", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int res = 0;
        for (int i = 0; i < a.length(); i++) res |= a.charAt(i) ^ b.charAt(i);
        return res == 0;
    }

    private static String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}



