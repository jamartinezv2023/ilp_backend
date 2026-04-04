package com.inclusive.authservice.security.tenant;

import com.inclusive.authservice.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TenantValidationFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String tenantHeader = request.getHeader(TENANT_HEADER);

        if (tenantHeader == null || tenantHeader.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("""
                { "error": "TENANT_HEADER_MISSING", "message": "Missing required header X-Tenant-Id" }
            """);
            return;
        }

        try {
            UUID tenantId = UUID.fromString(tenantHeader);
            TenantContext.setTenantId(tenantId);
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("""
                { "error": "INVALID_TENANT_ID", "message": "X-Tenant-Id must be a valid UUID" }
            """);
        } finally {
            TenantContext.clear();
        }
    }
}
