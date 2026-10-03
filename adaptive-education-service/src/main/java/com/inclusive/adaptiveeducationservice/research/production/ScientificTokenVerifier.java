package com.inclusive.adaptiveeducationservice.research.production;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.UUID;
/** RS256 verification with required issuer, audience, timestamps and institutional identity. */
public final class ScientificTokenVerifier {
    private final NimbusJwtDecoder decoder;
    public ScientificTokenVerifier(RSAPublicKey publicKey, String issuer, String audience) {
        if (issuer == null || audience == null || publicKey == null
                || issuer.isBlank() || audience.isBlank()
                || publicKey.getModulus().bitLength() < 2048) {
            throw new IllegalArgumentException("A trusted issuer, audience and RSA key are required");
        }
        decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer), jwt -> validateClaims(jwt, audience)));
    }
    private static OAuth2TokenValidatorResult validateClaims(Jwt jwt, String audience) {
        var expiresAt = jwt.getExpiresAt();
        var issuedAt = jwt.getIssuedAt();
        var audiences = jwt.getAudience();
        if (expiresAt == null || issuedAt == null || audiences == null) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        }
        if (issuedAt.isAfter(Instant.now().plusSeconds(60))
                || !expiresAt.isAfter(issuedAt)
                || !audiences.contains(audience)) {
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
        var subject = jwt.getSubject();
        var tenantId = jwt.getClaimAsString("tenantId");
        if (subject == null || tenantId == null
                || subject.isBlank() || tenantId.isBlank()) {
            throw new JwtException("Invalid actor claims");
        }
        try {
            return new ScientificActor(UUID.fromString(subject), UUID.fromString(tenantId));
        } catch (IllegalArgumentException exception) {
            throw new JwtException("Invalid actor claims", exception);
        }
    }
}
