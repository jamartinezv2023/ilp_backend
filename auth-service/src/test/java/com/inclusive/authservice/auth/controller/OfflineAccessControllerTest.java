package com.inclusive.authservice.auth.controller;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfflineAccessControllerTest {
    private final SessionIdentityController identities = mock(SessionIdentityController.class);
    private final UUID owner = UUID.fromString("90000000-0000-4000-8000-000000000001");
    private final UUID tenant = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private final Jwt principal = Jwt.withTokenValue("synthetic").header("alg", "RS256").subject(owner.toString()).build();
    private OfflineAccessController controller;
    private RSAPublicKey publicKey;
    private OfflineAccessController.Enrollment request;
    @BeforeEach void setup() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA"); generator.initialize(2048);
        var pair = generator.generateKeyPair(); publicKey = (RSAPublicKey) pair.getPublic();
        controller = new OfflineAccessController(identities, Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()), (RSAPublicKey) generator.generateKeyPair().getPublic());
        when(identities.identity(principal)).thenReturn(ResponseEntity.ok(new SessionIdentityController.SessionIdentity(owner, tenant)));
        request = new OfflineAccessController.Enrollment(UUID.randomUUID().toString(), "r9-v1", UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }
    @Test void signsBoundShortLivedIdentityWithoutPasswordsAndDisablesCaching() throws Exception {
        var response = controller.enroll(principal, request);
        assertEquals("no-store", response.getHeaders().getFirst("Cache-Control"));
        var token = SignedJWT.parse(response.getBody().credential());
        assertTrue(token.verify(new RSASSAVerifier(publicKey)));
        var claims = token.getJWTClaimsSet();
        assertEquals(owner.toString(), claims.getSubject()); assertEquals(tenant.toString(), claims.getStringClaim("tenantId"));
        assertEquals(request.deviceId(), claims.getStringClaim("deviceId"));
        assertEquals(request.administrationId(), claims.getStringClaim("administrationId"));
        assertEquals(600000, claims.getExpirationTime().getTime() - claims.getIssueTime().getTime());
        assertFalse(claims.getClaims().containsKey("password"));
        assertEquals("ilp-offline+jwt", token.getHeader().getType().toString());
    }
    @Test void anotherApiSigningKeyCannotVerifyTheOfflineCredential() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA"); generator.initialize(2048);
        var normalApiKey = (RSAPublicKey) generator.generateKeyPair().getPublic();
        var credential = controller.enroll(principal, request).getBody().credential();
        var decoder = NimbusJwtDecoder.withPublicKey(normalApiKey).build();
        assertThrows(org.springframework.security.oauth2.jwt.JwtException.class,
                () -> decoder.decode(credential));
    }
    @Test void accountRejectionBlocksEnrollment() {
        when(identities.identity(principal)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN));
        assertThrows(ResponseStatusException.class, () -> controller.enroll(principal, request));
    }
    @Test void rejectsInvalidContext() {
        var invalid = new OfflineAccessController.Enrollment("bad", "r9-v1", request.administrationId(), request.deviceId());
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> controller.enroll(principal, invalid)).getStatusCode().value());
    }
    @Test void rejectsMissingRequest() { assertThrows(ResponseStatusException.class, () -> controller.enroll(principal, null)); }
    @Test void rejectsMissingVersion() {
        var invalid = new OfflineAccessController.Enrollment(request.assignmentId(), null, request.administrationId(), request.deviceId());
        assertThrows(ResponseStatusException.class, () -> controller.enroll(principal, invalid));
    }
    @Test void rejectsNoncanonicalDeviceAndOverlongVersion() {
        var invalid = new OfflineAccessController.Enrollment(request.assignmentId(), "a".repeat(101), request.administrationId(), "1-1-1-1-1");
        assertThrows(ResponseStatusException.class, () -> controller.enroll(principal, invalid));
    }
    @Test void rejectsReuseOfTheApiKey() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA"); generator.initialize(2048);
        var pair = generator.generateKeyPair();
        var encoded = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        var apiKey = (RSAPublicKey) pair.getPublic();
        assertThrows(IllegalArgumentException.class, () -> new OfflineAccessController(identities, encoded, apiKey));
    }
    @Test void rejectsWeakKeys() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA"); generator.initialize(1024);
        var pair = generator.generateKeyPair();
        var encoded = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        assertThrows(IllegalArgumentException.class, () -> new OfflineAccessController(identities, encoded, publicKey));
    }
    @Test void rejectsMissingIdentityBody() {
        when(identities.identity(principal)).thenReturn(ResponseEntity.ok().build());
        assertThrows(ResponseStatusException.class, () -> controller.enroll(principal, request));
    }
    @Test void rejectsMissingDevice() {
        var invalid = new OfflineAccessController.Enrollment(request.assignmentId(), "r9-v1", request.administrationId(), null);
        assertThrows(ResponseStatusException.class, () -> controller.enroll(principal, invalid));
    }
    @Test void rejectsNoncanonicalAdministration() {
        var invalid = new OfflineAccessController.Enrollment(request.assignmentId(), "r9-v1", "1-1-1-1-1", request.deviceId());
        assertThrows(ResponseStatusException.class, () -> controller.enroll(principal, invalid));
    }
    @Test void rejectsMisconfiguredKeys() { assertThrows(IllegalArgumentException.class, () -> new OfflineAccessController(identities, "invalid", publicKey)); }
}
