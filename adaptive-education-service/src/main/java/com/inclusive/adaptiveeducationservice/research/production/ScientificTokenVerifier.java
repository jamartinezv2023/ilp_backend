package com.inclusive.adaptiveeducationservice.research.production;

import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/** RS256 verification with required issuer, audience, timestamps and institutional identity. */
public final class ScientificTokenVerifier {
    private final NimbusJwtDecoder decoder;

    public ScientificTokenVerifier(RSAPublicKey publicKey, String issuer, String audience) {
        if (issuer.isBlank() || audience.isBlank() || publicKey.getModulus().bitLength() < 2048) {
            throw new IllegalArgumentException("A trusted issuer, audience and RSA key are required");
        }
        decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer), jwt -> validateClaims(jwt, audience)));
    }

    private static OAuth2TokenValidatorResult validateClaims(Jwt jwt, String audience) {
        if (jwt.getExpiresAt() == null || jwt.getIssuedAt() == null
                || jwt.getIssuedAt().isAfter(java.time.Instant.now().plusSeconds(60))
                || !jwt.getExpiresAt().isAfter(jwt.getIssuedAt())
                || !jwt.getAudience().contains(audience)) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        }
        return OAuth2TokenValidatorResult.success();
    }

    public ScientificActor verify(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")
                || authorization.length() > 16384) {
            throw new JwtException("Bearer token required");
        }
        Jwt jwt = decoder.decode(authorization.substring(7));
        try {
            return new ScientificActor(UUID.fromString(jwt.getSubject()),
                    UUID.fromString(jwt.getClaimAsString("tenantId")));
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new JwtException("Invalid actor claims", exception);
        }
    }
}
