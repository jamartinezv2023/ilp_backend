package com.inclusive.authservice.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthoritiesExtractor {

    public Collection<? extends GrantedAuthority> extract(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            for (String role : roles) {
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        List<String> permissions = jwt.getClaimAsStringList("permissions");
        if (permissions != null) {
            for (String permission : permissions) {
                authorities.add(new SimpleGrantedAuthority("PERM_" + permission));
            }
        }

        return authorities;
    }
}
