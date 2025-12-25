// Location: auth-service/src/main/java/com/inclusive/authservice/tenant/TenantFilter.java
package com.inclusive.authservice.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro responsable de extraer y validar el tenant por request.
 *
 * ✔ Se ejecuta una sola vez por request
 * ✔ No depende de servicios ni repositorios
 * ✔ Compatible con WebMvcTest y SpringBootTest
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId == null || tenantId.isBlank()) {
            response.sendError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Missing X-Tenant-Id header"
            );
            return;
        }

        try {
            TenantContext.setTenantId(tenantId);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
