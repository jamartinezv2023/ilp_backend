// UbicaciÃƒÆ’Ã‚Â³n:
// src/main/java/com/inclusive/authservice/multitenancy/filter/TenantHeaderFilter.java

package com.inclusive.authservice.multitenancy.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Tenant validation filter.
 *
 * Enforces the presence of X-Tenant-Id header for all protected endpoints.
 *
 * IMPORTANT:
 * - JWKS endpoint MUST be publicly accessible (OAuth2 standard)
 * - Actuator endpoints are excluded
 * - Login endpoint is excluded
 */
public class TenantHeaderFilter extends OncePerRequestFilter {


    private static final String TENANT_HEADER = "X-Tenant-Id";

    /**
     * Paths that MUST NOT require tenant header.
     */
    private static final Set<String> EXCLUDED_PATHS = Set.of(
        "/.well-known/jwks.json",
        "/auth/login"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Exact matches
        if (EXCLUDED_PATHS.contains(path)) {
            return true;
        }

        // Actuator endpoints
        return path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId == null || tenantId.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                {
                  "error": "TENANT_HEADER_MISSING",
                  "message": "Missing required header X-Tenant-Id"
                }
                """);
            return;
        }

        // AquÃƒÆ’Ã‚Â­ puedes setear el tenant en ThreadLocal si lo usas
        // TenantContext.setCurrentTenant(tenantId);

        filterChain.doFilter(request, response);
    }
}
