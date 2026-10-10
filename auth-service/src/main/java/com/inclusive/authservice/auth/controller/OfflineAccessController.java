package com.inclusive.authservice.auth.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.KeyFactory;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.Map;
import java.util.regex.Pattern;

/** Prepared local identity only. Uses a separate signing key, never an API bearer key. */
@RestController
@ConditionalOnProperty(name = "security.offline.enabled", havingValue = "true")
public final class OfflineAccessController {
    private static final Pattern INSTRUMENT_VERSION = Pattern.compile("[A-Za-z0-9._-]{1,100}");
    private final SessionIdentityController identities;
    private final RSAPrivateKey signingKey;
    public record Enrollment(String assignmentId, String instrumentVersion, String administrationId, String deviceId) {}
    public record Credential(String credential) {}

    public OfflineAccessController(SessionIdentityController identities,
            @Value("${security.offline.private-key}") String encodedKey, RSAPublicKey apiPublicKey) {
        this.identities = identities;
        try {
            this.signingKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedKey)));
            if (signingKey.getModulus().bitLength() < 2048 || signingKey.getModulus().equals(apiPublicKey.getModulus())) {
                throw new IllegalArgumentException("Weak offline signing key");
            }
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid separate offline signing key", ex);
        }
    }
    @PostMapping("/auth/offline-access")
    public ResponseEntity<Credential> enroll(@AuthenticationPrincipal Jwt principal, @RequestBody Enrollment input) {
        var identity = identities.identity(principal).getBody();
        if (identity == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (input == null || !canonicalUuid(input.assignmentId()) || !canonicalUuid(input.administrationId())
                || !canonicalUuid(input.deviceId()) || input.instrumentVersion() == null
                || !INSTRUMENT_VERSION.matcher(input.instrumentVersion()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid offline enrollment");
        }
        Instant issued = Instant.now();
        var claims = new JWTClaimsSet.Builder().issuer("urn:ilp:offline:v1").audience("ilp-local-edit")
                .subject(identity.userId().toString()).claim("tenantId", identity.tenantId().toString())
                .claim("assignmentId", input.assignmentId()).claim("instrumentVersion", input.instrumentVersion())
                .claim("administrationId", input.administrationId()).claim("deviceId", input.deviceId())
                .claim("iat", issued.getEpochSecond()).claim("exp", issued.plusSeconds(600).getEpochSecond())
                .jwtID(UUID.randomUUID().toString()).build();
        var token = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(new JOSEObjectType("ilp-offline+jwt")).build(), claims);
        try { token.sign(new RSASSASigner(signingKey)); }
        catch (JOSEException ex) { throw new IllegalStateException("Offline credential signing failed", ex); }
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(new Credential(token.serialize()));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> rejectedEnrollment(ResponseStatusException failure) {
        return rejection(failure.getStatusCode());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> unreadableEnrollment(HttpMessageNotReadableException failure) {
        return rejection(HttpStatus.BAD_REQUEST);
    }
    private static ResponseEntity<Map<String, String>> rejection(HttpStatusCode status) {
        return ResponseEntity.status(status).header("Cache-Control", "no-store")
                .body(Map.of("error", "OFFLINE_ENROLLMENT_REJECTED"));
    }
    private static boolean canonicalUuid(String value) {
        if (value == null) {
            return false;
        }
        try { return UUID.fromString(value).toString().equals(value); }
        catch (IllegalArgumentException ex) { return false; }
    }
}
