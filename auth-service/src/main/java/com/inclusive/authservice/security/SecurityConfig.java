package com.inclusive.authservice.security;

import com.inclusive.authservice.security.jwt.JwtAuthConverter;
import com.inclusive.authservice.security.tenant.TenantValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            TenantValidationFilter tenantFilter,
            JwtAuthConverter jwtAuthConverter
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/.well-known/jwks.json",
                    "/auth/login",
                    "/actuator/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
            );

        return http.build();
    }
}
