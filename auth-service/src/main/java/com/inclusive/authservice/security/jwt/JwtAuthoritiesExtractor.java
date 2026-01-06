// Location: auth-service/src/main/java/com/inclusive/authservice/security/jwt/JwtAuthoritiesExtractor.java
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

        Object roles = jwt.getClaims().get("roles");
        if (roles instanceof Collection<?> roleList) {
            for (Object r : roleList) {
                String role = String.valueOf(r);
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        Object permissions = jwt.getClaims().get("permissions");
        if (permissions instanceof Collection<?> permList) {
            for (Object p : permList) {
                authorities.add(
                        new SimpleGrantedAuthority("PERM_" + p)
                );
            }
        }

        return authorities;
    }
}
