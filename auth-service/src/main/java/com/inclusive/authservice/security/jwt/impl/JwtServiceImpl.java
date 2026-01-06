// Location: auth-service/src/main/java/com/inclusive/authservice/security/jwt/impl/JwtServiceImpl.java
package com.inclusive.authservice.security.jwt.impl;

import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.issuer:ilp-auth-service}")
    private String issuer;

    @Value("${security.jwt.access-token-validity-seconds:900}")
    private long accessTokenValiditySeconds;

    @Override
    public String generateAccessToken(UserAccount user, UUID tenantId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenValiditySeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("tenant_id", tenantId.toString())
                .claim("email", user.getEmail())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    @Override
    public long getAccessTokenExpirationSeconds() {
        return accessTokenValiditySeconds;
    }
}
