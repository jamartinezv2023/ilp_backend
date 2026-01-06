package com.inclusive.authservice.security.jwt;

import com.inclusive.authservice.entity.UserAccount;

import java.util.UUID;

public interface JwtService {

    /**
     * Genera un Access Token JWT para un usuario dentro de un tenant específico.
     */
    String generateAccessToken(UserAccount user, UUID tenantId);

    /**
     * Tiempo de expiración del access token en segundos.
     */
    long getAccessTokenExpirationSeconds();
}
