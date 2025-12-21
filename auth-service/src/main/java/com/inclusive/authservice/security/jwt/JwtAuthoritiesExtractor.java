package com.inclusive.authservice.security.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtAuthoritiesExtractor {

    public Collection<SimpleGrantedAuthority> extract(Jwt jwt) {
        Set<SimpleGrantedAuthority> out = new LinkedHashSet<>();

        // 1) ROLES -> ROLE_*
        out.addAll(extractRoles(jwt));

        // 2) SCOPES -> SCOPE_*
        out.addAll(extractScopes(jwt));

        // 3) TENANT -> TENANT_<id> (opcional)
        String tenant = jwt.getClaimAsString("tenant");
        if (tenant != null && !tenant.isBlank()) {
            out.add(new SimpleGrantedAuthority("TENANT_" + tenant));
        }

        return out;
    }

    private Collection<SimpleGrantedAuthority> extractRoles(Jwt jwt) {
        Object rolesClaim = jwt.getClaims().get("roles");
        if (rolesClaim == null) rolesClaim = jwt.getClaims().get("role");

        List<String> roles = new ArrayList<>();

        if (rolesClaim instanceof String s) {
            roles.add(s);
        } else if (rolesClaim instanceof Collection<?> col) {
            for (Object o : col) {
                if (o != null) roles.add(o.toString());
            }
        }

        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        for (String r : roles) {
            String role = r.trim();
            if (role.isEmpty()) continue;
            if (!role.startsWith("ROLE_")) role = "ROLE_" + role;
            auths.add(new SimpleGrantedAuthority(role));
        }
        return auths;
    }

    private Collection<SimpleGrantedAuthority> extractScopes(Jwt jwt) {
        Set<String> scopes = new LinkedHashSet<>();

        // scope: "read write"
        String scopeStr = jwt.getClaimAsString("scope");
        if (scopeStr != null && !scopeStr.isBlank()) {
            scopes.addAll(Arrays.asList(scopeStr.trim().split("\\s+")));
        }

        // scp: ["read","write"] (Azure AD style)
        Object scp = jwt.getClaims().get("scp");
        if (scp instanceof Collection<?> col) {
            for (Object o : col) {
                if (o != null) scopes.add(o.toString());
            }
        }

        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        for (String s : scopes) {
            String scope = s.trim();
            if (scope.isEmpty()) continue;
            if (!scope.startsWith("SCOPE_")) scope = "SCOPE_" + scope;
            auths.add(new SimpleGrantedAuthority(scope));
        }
        return auths;
    }
}

