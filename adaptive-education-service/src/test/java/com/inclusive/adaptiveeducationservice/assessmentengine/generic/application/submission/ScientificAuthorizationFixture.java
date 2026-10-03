package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.research.application.ScientificApplicationGrant;
import com.inclusive.adaptiveeducationservice.research.application.ScientificApplicationGrantPort;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/** Loopback laboratory only: keys and assignments never enter production resources. */
final class ScientificAuthorizationFixture implements ScientificApplicationGrantPort {
    static final UUID USER = UUID.fromString("22222222-2222-2222-2222-222222222222");
    static final UUID TENANT = UUID.fromString("33333333-3333-3333-3333-333333333333");
    static final UUID GRANT = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final JdbcTemplate jdbc;
    private final RSAPrivateKey signingKey;
    private final NimbusJwtDecoder decoder;

    ScientificAuthorizationFixture(JdbcTemplate jdbc) throws Exception {
        this.jdbc = jdbc;
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var pair = generator.generateKeyPair();
        signingKey = (RSAPrivateKey) pair.getPrivate();
        decoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) pair.getPublic()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer("urn:ilp:synthetic-auth"),
                token -> token.getExpiresAt() != null && token.getAudience() != null
                        && token.getAudience().contains("ilp-scientific-lab")
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"))));
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS scientific_lab_grants (
                    id varchar(36) PRIMARY KEY, user_id varchar(36), tenant_id varchar(36),
                    student_id varchar(255), research_uuid varchar(36), subject_id varchar(36),
                    consent_id varchar(36), consent_version varchar(255), consent_type varchar(255), instrument varchar(255),
                    instrument_version varchar(255), active boolean
                )
                """);
        jdbc.update("DELETE FROM scientific_lab_grants");
    }

    void save(ScientificApplicationGrant grant) {
        jdbc.update("""
                INSERT INTO scientific_lab_grants VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, grant.id().toString(), grant.userId().toString(), grant.tenantId().toString(),
                grant.studentId(), grant.researchParticipantUuid().toString(), grant.researchSubjectId(),
                grant.consentId().toString(), grant.consentVersion(), grant.consentType(), grant.assessmentCode(),
                grant.assessmentVersion(), grant.active());
    }

    @Override
    public Optional<ScientificApplicationGrant> findById(UUID id) {
        return jdbc.query("SELECT * FROM scientific_lab_grants WHERE id = ?", (row, index) ->
                new ScientificApplicationGrant(UUID.fromString(row.getString("id")),
                        UUID.fromString(row.getString("user_id")), UUID.fromString(row.getString("tenant_id")),
                        row.getString("student_id"), UUID.fromString(row.getString("research_uuid")),
                        row.getString("subject_id"), UUID.fromString(row.getString("consent_id")),
                        row.getString("consent_version"), row.getString("consent_type"), row.getString("instrument"),
                        row.getString("instrument_version"), row.getBoolean("active")), id.toString())
                .stream().findFirst();
    }

    String token(UUID user, UUID tenant, String audience, Instant expires) throws Exception {
        var claims = new JWTClaimsSet.Builder().issuer("urn:ilp:synthetic-auth")
                .subject(user.toString()).audience(audience).issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(expires)).claim("tenantId", tenant.toString()).build();
        var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        jwt.sign(new RSASSASigner(signingKey));
        return jwt.serialize();
    }

    Jwt verify(String token) {
        return decoder.decode(token);
    }
}
