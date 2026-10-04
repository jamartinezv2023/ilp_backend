package com.inclusive.adaptiveeducationservice.research.production;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificTokenVerifierTest {
    private static KeyPair keys;
    private static final UUID USER = UUID.fromString("00000000-0000-4000-8000-000000000001");
    private static final UUID TENANT = UUID.fromString("00000000-0000-4000-8000-000000000002");

    @BeforeAll
    static void generateKey() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keys = generator.generateKeyPair();
    }

    private ScientificTokenVerifier verifier() {
        return new ScientificTokenVerifier((RSAPublicKey) keys.getPublic(), "urn:ilp:test", "ilp-scientific-api");
    }

    private JWTClaimsSet.Builder claims() {
        return new JWTClaimsSet.Builder().issuer("urn:ilp:test").audience("ilp-scientific-api")
                .subject(USER.toString()).claim("tenantId", TENANT.toString())
                .issueTime(Date.from(Instant.now().minusSeconds(10)))
                .expirationTime(Date.from(Instant.now().plusSeconds(300)));
    }

    private String sign(JWTClaimsSet.Builder builder) throws Exception {
        var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), builder.build());
        jwt.sign(new RSASSASigner(keys.getPrivate()));
        return "Bearer " + jwt.serialize();
    }

    @Test
    void shouldExtractOnlySignedInstitutionalIdentity() throws Exception {
        var token = sign(claims());
        assertThat(verifier().verify(token)).isEqualTo(new ScientificActor(USER, TENANT));
    }

    @Test
    void shouldRejectMissingMalformedAndOversizedBearer() {
        var verifier = verifier();
        assertThatThrownBy(() -> verifier.verify(null)).isInstanceOf(JwtException.class);
        for (var header : List.of("Basic abc", "Bearer invalid", "Bearer " + "x".repeat(16384))) {
            assertThatThrownBy(() -> verifier.verify(header)).isInstanceOf(JwtException.class);
        }
    }

    @Test
    void shouldRejectUntrustedClaimsEvenWhenSignatureIsValid() throws Exception {
        var invalid = List.of(
                claims().issuer("urn:other"),
                claims().audience("other-api"),
                claims().audience(List.of()),
                claims().expirationTime(null),
                claims().issueTime(null),
                claims().issueTime(Date.from(Instant.now().plusSeconds(120))),
                claims().expirationTime(Date.from(Instant.now().minusSeconds(300))),
                claims().expirationTime(Date.from(Instant.now().minusSeconds(20))),
                claims().subject(null),
                claims().subject(" "),
                claims().subject("invalid-uuid"),
                claims().claim("tenantId", null),
                claims().claim("tenantId", " "),
                claims().claim("tenantId", "invalid-uuid"));
        var verifier = verifier();
        for (var builder : invalid) {
            var token = sign(builder);
            assertThatThrownBy(() -> verifier.verify(token)).isInstanceOf(JwtException.class);
        }
    }

    @Test
    void shouldRejectMissingTrustConfigurationAndWeakKeys() throws Exception {
        var key = (RSAPublicKey) keys.getPublic();
        assertThatThrownBy(() -> new ScientificTokenVerifier(key, null, "audience"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ScientificTokenVerifier(key, "issuer", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ScientificTokenVerifier(null, "issuer", "audience"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ScientificTokenVerifier(key, " ", "audience"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ScientificTokenVerifier(key, "issuer", " "))
                .isInstanceOf(IllegalArgumentException.class);
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        var weakKey = (RSAPublicKey) generator.generateKeyPair().getPublic();
        assertThatThrownBy(() -> new ScientificTokenVerifier(weakKey, "issuer", "audience"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
