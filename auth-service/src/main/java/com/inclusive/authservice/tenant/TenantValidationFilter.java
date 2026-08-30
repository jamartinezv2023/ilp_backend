package com.inclusive.authservice.security.tenant;

import com.inclusive.authservice.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TenantValidationFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        TenantContext.clear();
        try {
            // Preserve the existing actuator exemption; this patch does not change CORS.
            if (request.getRequestURI().startsWith("/actuator")) {
                filterChain.doFilter(request, response);
                return;
            }
            String tenantHeader = request.getHeader(TENANT_HEADER);
            if (tenantHeader == null || tenantHeader.isBlank()) {
                reject(response, HttpServletResponse.SC_BAD_REQUEST, "TENANT_HEADER_MISSING");
                return;
            }
            UUID headerTenant;
            try {
                headerTenant = UUID.fromString(tenantHeader);
                if (!headerTenant.toString().equalsIgnoreCase(tenantHeader)) {
                    throw new IllegalArgumentException("Non-canonical UUID");
                }
            } catch (IllegalArgumentException ex) {
                reject(response, HttpServletResponse.SC_BAD_REQUEST, "INVALID_TENANT_ID");
                return;
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
                UUID tokenTenant;
                try {
                    String claim = jwtAuthentication.getToken().getClaimAsString("tenantId");
                    tokenTenant = UUID.fromString(claim);
                    if (!tokenTenant.toString().equalsIgnoreCase(claim)) {
                        throw new IllegalArgumentException("Non-canonical tenant claim");
                    }
                } catch (IllegalArgumentException | NullPointerException ex) {
                    reject(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN_TENANT");
                    return;
                }
                if (!tokenTenant.equals(headerTenant)) {
                    reject(response, HttpServletResponse.SC_FORBIDDEN, "TENANT_MISMATCH");
                    return;
                }
            }
            TenantContext.setTenantId(headerTenant);
            // Deliberately outside the UUID catch: downstream failures retain their meaning.
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void reject(HttpServletResponse response, int status, String code) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + code + "\"}");
    }
}
