// Location: auth-service/src/main/java/com/inclusive/authservice/security/jwt/JwtAuthConverter.java
package com.inclusive.authservice.security.jwt;

import com.inclusive.authservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtAuthoritiesExtractor authoritiesExtractor;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String tenantId = jwt.getClaimAsString("tenant_id");
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("JWT missing tenant_id");
        }

        TenantContext.setTenantId(tenantId);

        Collection<? extends GrantedAuthority> authorities =
                authoritiesExtractor.extract(jwt);

        String principal = jwt.getSubject();

        return new JwtAuthenticationToken(jwt, authorities, principal);
    }
}
