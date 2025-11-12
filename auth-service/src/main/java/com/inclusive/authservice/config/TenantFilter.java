package com.inclusive.authservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro HTTP que obtiene el tenant desde el header "X-Tenant-ID"
 * y lo pasa al CurrentTenantIdentifierResolver
 */
@Component("tenantFilter")
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private final SchemaCurrentTenantIdentifierResolver tenantResolver;

    public TenantFilter(SchemaCurrentTenantIdentifierResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String tenantId = request.getHeader(TENANT_HEADER);
            if (tenantId != null && !tenantId.isBlank()) {
                tenantResolver.setCurrentTenant(tenantId);
            } else {
                tenantResolver.setCurrentTenant("public");
            }

            filterChain.doFilter(request, response);

        } finally {
            tenantResolver.clear();
        }
    }
}




