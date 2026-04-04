/*
 * auth-service/src/main/java/com/inclusive/authservice/auth/dto/AuthResponse.java
 * ILP Backend â€“ Authentication Service
 */
package com.inclusive.authservice.auth.dto;

public record AuthResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds,
        String refreshToken
) {}
