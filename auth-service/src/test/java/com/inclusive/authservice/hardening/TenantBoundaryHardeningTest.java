package com.inclusive.authservice.hardening;

import com.inclusive.authservice.security.jwt.JwtAuthConverter;
import com.inclusive.authservice.security.jwt.JwtAuthoritiesExtractor;
import com.inclusive.authservice.security.tenant.TenantValidationFilter;
import com.inclusive.authservice.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class TenantBoundaryHardeningTest {
    private static final UUID TENANT = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private final TenantValidationFilter filter = new TenantValidationFilter();

    @AfterEach
    void clear() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletRequest request(String header) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/login");
        if (header != null) {
            request.addHeader("X-Tenant-Id", header);
        }
        return request;
    }

    private void token(String tenant) {
        Jwt.Builder builder = Jwt.withTokenValue("fixture-token").header("alg", "RS256")
                .subject("22222222-2222-4222-8222-222222222222");
        if (tenant != null) {
            builder.claim("tenantId", tenant);
        }
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(builder.build()));
    }

    @Test
    void publicLoginReceivesHeaderContextAndAlwaysClearsIt() throws Exception {
        filter.doFilter(request(TENANT.toString()), new MockHttpServletResponse(),
                (req, res) -> assertEquals(TENANT, TenantContext.getTenantId()));
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void matchingJwtTenantIsAllowed() throws Exception {
        token(TENANT.toString());
        AtomicBoolean invoked = new AtomicBoolean();
        filter.doFilter(request(TENANT.toString()), new MockHttpServletResponse(), (req, res) -> {
            assertEquals(TENANT, TenantContext.getTenantId());
            invoked.set(true);
        });
        assertTrue(invoked.get());
        assertNull(TenantContext.getTenantId());
    }

    private void rejected(String header, int status) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> fail("Rejected request reached application");
        TenantContext.setTenantId(UUID.randomUUID());
        filter.doFilter(request(header), response, chain);
        assertEquals(status, response.getStatus());
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void mismatchedTenantIsForbidden() throws Exception {
        token(UUID.randomUUID().toString());
        rejected(TENANT.toString(), 403);
    }

    @Test
    void absentTokenTenantIsUnauthorized() throws Exception {
        token(null);
        rejected(TENANT.toString(), 401);
    }

    @Test
    void malformedTokenTenantIsUnauthorized() throws Exception {
        token("bad-tenant");
        rejected(TENANT.toString(), 401);
    }

    @Test
    void malformedHeaderIsBadRequest() throws Exception {
        rejected("not-a-uuid", 400);
        rejected("1-1-1-1-1", 400);
    }

    @Test
    void missingHeaderIsBadRequest() throws Exception {
        rejected(null, 400);
    }

    @Test
    void downstreamIllegalArgumentIsNotMisclassifiedAsUuidFailure() {
        IllegalArgumentException downstream = new IllegalArgumentException("domain failure");
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertSame(downstream, assertThrows(IllegalArgumentException.class,
                () -> filter.doFilter(request(TENANT.toString()), response, (req, res) -> {
                    throw downstream;
                })));
        assertEquals(200, response.getStatus());
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void converterNeverOverwritesThreadTenant() {
        TenantContext.setTenantId(TENANT);
        Jwt jwt = Jwt.withTokenValue("fixture").header("alg", "RS256").subject("fixture-user")
                .claim("tenantId", UUID.randomUUID().toString()).build();
        new JwtAuthConverter(new JwtAuthoritiesExtractor()).convert(jwt);
        assertEquals(TENANT, TenantContext.getTenantId());
    }

    @Test
    void actuatorExemptionStillClearsStaleContext() throws Exception {
        TenantContext.setTenantId(TENANT);
        filter.doFilter(new MockHttpServletRequest("GET", "/actuator/health"),
                new MockHttpServletResponse(), (req, res) -> assertNull(TenantContext.getTenantId()));
        assertNull(TenantContext.getTenantId());
    }
}
