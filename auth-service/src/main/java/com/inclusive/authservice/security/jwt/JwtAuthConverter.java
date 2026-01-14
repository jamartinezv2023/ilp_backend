package com.inclusive.authservice.security.jwt;

import com.inclusive.authservice.tenant.TenantContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String tenantIdClaim = jwt.getClaimAsString("tenant_id");

        if (tenantIdClaim != null) {
            TenantContext.setTenantId(UUID.fromString(tenantIdClaim));
        }

        return new JwtAuthenticationToken(jwt);
    }
}
