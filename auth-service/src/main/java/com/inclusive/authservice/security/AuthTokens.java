// src/main/java/com/inclusive/authservice/security/AuthTokens.java
package com.inclusive.authservice.security;

import java.time.Instant;

public class AuthTokens {

    private final String accessToken;
    private final String refreshToken;
    private final Instant accessTokenExpiresAt;
    private final Instant refreshTokenExpiresAt;

    public AuthTokens(String accessToken,
                      String refreshToken,
                      Instant accessTokenExpiresAt,
                      Instant refreshTokenExpiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Instant getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public Instant getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }
}
