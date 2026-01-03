package com.inclusive.authservice.security.jwt.impl;

import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.security.jwt.JwtService;
import com.inclusive.authservice.security.jwt.JwtProperties;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties props;

    public JwtServiceImpl(JwtEncoder jwtEncoder, JwtProperties props) {
        this.jwtEncoder = jwtEncoder;
        this.props = props;
    }

    @Override
    public String generateToken(
            UserAccount user,
            UUID tenantId,
            List<UUID> roleIds
    ) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.getIssuer())
                .audience(List.of(props.getAudience()))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(props.getAccessTokenExpirationSeconds()))
                .subject(user.getId().toString())
                .claim("tenant_id", tenantId.toString())
                .claim("roles", roleIds)
                .build();

        return jwtEncoder.encode(
                org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(claims)
        ).getTokenValue();
    }

    @Override
    public long getAccessTokenExpirationSeconds() {
        return props.getAccessTokenExpirationSeconds();
    }
}
