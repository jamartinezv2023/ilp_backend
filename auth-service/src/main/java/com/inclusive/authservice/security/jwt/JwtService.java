package com.inclusive.authservice.security.jwt;

import java.util.Set;
import java.util.UUID;

public interface JwtService {

    String generateAccessToken(
            UUID userId,
            UUID tenantId,
            String email,
            Set<String> roles,
            Set<String> permissions
    );

    String generateRefreshToken();
}
