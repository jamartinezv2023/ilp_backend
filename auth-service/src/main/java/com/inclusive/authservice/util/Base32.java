package com.inclusive.authservice.util;

/**
 * ImplementaciÃ³n sencilla de Base32 (RFC 3548) sin dependencias externas.
 * SÃ³lo lo necesario para TOTP (decode/encode).
 */
public final class Base32 {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int[] LOOKUP = new int[256];
    static {
        for (int i = 0; i < LOOKUP.length; i++) LOOKUP[i] = -1;
        for (int i = 0; i < ALPHABET.length(); i++) LOOKUP[ALPHABET.charAt(i)] = i;
        for (int i = 0; i < ALPHABET.length(); i++) LOOKUP[Character.toLowerCase(ALPHABET.charAt(i))] = i;
    }
    private Base32() {}

    public static String encode(byte[] data) {
        StringBuilder sb = new StringBuilder((data.length * 8 + 4) / 5);
        int buffer = 0, bitsLeft = 0;
        for (byte b : data) {
            buffer <<= 8;
            buffer |= (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                sb.append(ALPHABET.charAt((buffer >> (bitsLeft - 5)) & 0x1F));
                bitsLeft -= 5;
            }
        }
        if (bitsLeft > 0) {
            sb.append(ALPHABET.charAt((buffer << (5 - bitsLeft)) & 0x1F));
        }
        return sb.toString();
    }

    public static byte[] decode(String s) {
        int buffer = 0, bitsLeft = 0;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        for (char c : s.toCharArray()) {
            if (c == '=' || c == ' ' || c == '\n' || c == '\r' || c == '\t') continue;
            int val = c < 256 ? LOOKUP[c] : -1;
            if (val < 0) throw new IllegalArgumentException("Invalid Base32 char: " + c);
            buffer <<= 5;
            buffer |= val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out.write((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }
        return out.toByteArray();
    }
}



