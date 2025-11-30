package com.inclusive.authservice.service;

import com.inclusive.authservice.entity.RefreshToken;

import java.time.Instant;

/**
 * Servicio para gestionar refresh tokens persistidos.
 */
public interface RefreshTokenService {

    RefreshToken createRefreshToken(Long userId,
                                    String token,
                                    Instant expiresAt,
                                    String createdByIp);

    void revokeToken(String token,
                     String revokedByIp,
                     String reason);

    void revokeAllUserTokens(Long userId,
                             String revokedByIp,
                             String reason);

    boolean isValid(String token);
}