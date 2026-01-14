package com.inclusive.authservice.security.jwt.impl;

import com.inclusive.authservice.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.access-token-minutes}")
    private long accessTokenMinutes;

    @Value("${security.jwt.refresh-token-days}")
    private long refreshTokenDays;

    @Override
    public String generateAccessToken(
            UUID userId,
            UUID tenantId,
            String email,
            Set<String> roles,
            Set<String> permissions
    ) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenMinutes * 60))
                .subject(userId.toString())
                .claim("tenantId", tenantId.toString())
                .claim("email", email)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    @Override
    public String generateRefreshToken() {
        // Refresh token simple, seguro y desacoplado
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID();
    }
}
