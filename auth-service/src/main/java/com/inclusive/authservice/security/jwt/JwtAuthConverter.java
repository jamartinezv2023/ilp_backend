package com.inclusive.authservice.security.jwt;

import com.inclusive.authservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtAuthoritiesExtractor authoritiesExtractor;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String tenantIdClaim = jwt.getClaimAsString("tenantId");

        if (tenantIdClaim != null) {
            TenantContext.setTenantId(UUID.fromString(tenantIdClaim));
        }

        return new JwtAuthenticationToken(
                jwt,
                authoritiesExtractor.extract(jwt)
        );
    }
}
