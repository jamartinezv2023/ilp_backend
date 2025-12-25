package com.inclusive.authservice.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtAuthoritiesExtractor extractor;

    public JwtAuthConverter(JwtAuthoritiesExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        var authorities = extractor.extract(jwt);
        // principalName: normalmente sub
        String principal = jwt.getSubject();
        return new JwtAuthenticationToken(jwt, authorities, principal);
    }
}
