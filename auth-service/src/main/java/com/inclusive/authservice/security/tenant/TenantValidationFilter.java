// Location: auth-service/src/main/java/com/inclusive/authservice/tenant/TenantValidationFilter.java
package com.inclusive.authservice.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Validates tenant header and stores it in TenantContext for the request lifecycle.
 */
@Component
public class TenantValidationFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String tenantId = request.getHeader(TENANT_HEADER);

        // Permit health/auth endpoints without tenant (optional; adjust if needed)
        String path = request.getRequestURI();
        boolean isPublic =
                path.startsWith("/auth/")
                        || path.startsWith("/actuator/");

        if (!isPublic) {
            if (tenantId == null || tenantId.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("""
                        {"error":"TENANT_HEADER_MISSING","message":"Missing X-Tenant-Id header"}
                        """);
                return;
            }

            // Optionally: format validation (UUID? slug?) -> for now just basic safety
            if (tenantId.length() > 64) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("""
                        {"error":"TENANT_HEADER_INVALID","message":"Invalid X-Tenant-Id header"}
                        """);
                return;
            }
        }

        try {
            // Store tenantId for the rest of the request
            if (tenantId != null && !tenantId.isBlank()) {
                TenantContext.setTenantId(tenantId.trim());
            }
            filterChain.doFilter(request, response);
        } finally {
            // CRITICAL: avoid leakage between requests/threads
            TenantContext.clear();
        }
    }
}
