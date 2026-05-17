package com.inclusive.authservice.security;

import com.inclusive.authservice.security.jwt.JwtAuthConverter;
import com.inclusive.authservice.security.jwt.JwtAuthoritiesExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthConverterTest {

    private final JwtAuthoritiesExtractor authoritiesExtractor =
            new JwtAuthoritiesExtractor();

    private final JwtAuthConverter converter =
            new JwtAuthConverter(authoritiesExtractor);

    @Test
    void shouldConvertRolesIntoGrantedAuthorities() {

        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "RS256"),
                Map.of(
                        "roles", List.of("USER", "ADMIN"),
                        "sub", "test-user"
                )
        );

        Authentication authentication = converter.convert(jwt);

        assertThat(authentication).isNotNull();

        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .contains(
                        "ROLE_USER",
                        "ROLE_ADMIN"
                );
    }

    @Test
    void shouldHandleMissingRolesGracefully() {

        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "RS256"),
                Map.of("sub", "test-user")
        );

        Authentication authentication = converter.convert(jwt);

        assertThat(authentication).isNotNull();

        assertThat(authentication.getAuthorities())
                .isEmpty();
    }
}
