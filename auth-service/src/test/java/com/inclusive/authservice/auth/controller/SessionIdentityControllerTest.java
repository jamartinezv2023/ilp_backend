package com.inclusive.authservice.auth.controller;

import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import com.inclusive.authservice.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SessionIdentityControllerTest {
    private final UUID owner = UUID.fromString("90000000-0000-4000-8000-000000000001");
    private final UUID tenant = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private final UserAccountRepository accounts = mock(UserAccountRepository.class);
    private final SessionIdentityController controller = new SessionIdentityController(accounts);
    @AfterEach void clearContext() { TenantContext.clear(); }
    private Jwt principal(String subject, String tenantClaim) {
        return Jwt.withTokenValue("test-only").header("alg", "RS256")
                .subject(subject).claim("tenantId", tenantClaim).build();
    }
    private UserAccount account(UUID institution, boolean enabled) {
        return new UserAccount(owner, institution, "synthetic@example.invalid", "test-only",
                enabled, false, null, Instant.now(), null);
    }
    @Test void returnsStableIdentityWithoutCredentialsAndDisablesCaching() {
        TenantContext.setTenantId(tenant);
        when(accounts.findById(owner)).thenReturn(Optional.of(account(tenant, true)));
        var response = controller.identity(principal(owner.toString(), tenant.toString()));
        assertEquals(new SessionIdentityController.SessionIdentity(owner, tenant), response.getBody());
        assertEquals("no-store", response.getHeaders().getFirst("Cache-Control"));
    }
    @Test void rejectsMissingPrincipal() {
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> controller.identity(null)).getStatusCode().value());
        verifyNoInteractions(accounts);
    }
    @Test void rejectsMalformedIdentity() {
        var jwt = principal("invalid", tenant.toString());
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> controller.identity(jwt)).getStatusCode().value());
        verifyNoInteractions(accounts);
    }
    @Test void rejectsMissingTenantClaim() {
        var jwt = Jwt.withTokenValue("test-only").header("alg", "RS256").subject(owner.toString()).build();
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> controller.identity(jwt)).getStatusCode().value());
        verifyNoInteractions(accounts);
    }
    @Test void rejectsNoncanonicalSubject() {
        var jwt = principal("1-1-1-1-1", tenant.toString());
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> controller.identity(jwt)).getStatusCode().value());
        verifyNoInteractions(accounts);
    }
    @Test void rejectsDifferentReturnedAccount() {
        TenantContext.setTenantId(tenant);
        var user = new UserAccount(UUID.randomUUID(), tenant, "synthetic@example.invalid", "test-only",
                true, false, null, Instant.now(), null);
        when(accounts.findById(owner)).thenReturn(Optional.of(user));
        var jwt = principal(owner.toString(), tenant.toString());
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> controller.identity(jwt)).getStatusCode().value());
    }
    @Test void rejectsTenantMismatchBeforeAccountRead() {
        TenantContext.setTenantId(UUID.randomUUID());
        var jwt = principal(owner.toString(), tenant.toString());
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> controller.identity(jwt)).getStatusCode().value());
        verifyNoInteractions(accounts);
    }
    @Test void rejectsDeletedAccount() {
        TenantContext.setTenantId(tenant);
        when(accounts.findById(owner)).thenReturn(Optional.empty());
        var jwt = principal(owner.toString(), tenant.toString());
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> controller.identity(jwt)).getStatusCode().value());
    }
    @Test void rejectsDisabledAccount() {
        TenantContext.setTenantId(tenant);
        when(accounts.findById(owner)).thenReturn(Optional.of(account(tenant, false)));
        var jwt = principal(owner.toString(), tenant.toString());
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> controller.identity(jwt)).getStatusCode().value());
    }
    @Test void rejectsAccountInDifferentInstitution() {
        TenantContext.setTenantId(tenant);
        when(accounts.findById(owner)).thenReturn(Optional.of(account(UUID.randomUUID(), true)));
        var jwt = principal(owner.toString(), tenant.toString());
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> controller.identity(jwt)).getStatusCode().value());
    }
}
