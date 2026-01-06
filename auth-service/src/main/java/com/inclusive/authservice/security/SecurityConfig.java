// Location: auth-service/src/main/java/com/inclusive/authservice/security/SecurityConfig.java
package com.inclusive.authservice.security;

import com.inclusive.authservice.security.jwt.JwtAuthConverter;
import com.inclusive.authservice.security.tenant.TenantValidationFilter; // ✅ IMPORT CORRECTO
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    private final TenantValidationFilter tenantValidationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            // Tenant FIRST
            .addFilterBefore(
                tenantValidationFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .oauth2ResourceServer(oauth ->
                oauth.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthConverter)
                )
            );

        return http.build();
    }
}
