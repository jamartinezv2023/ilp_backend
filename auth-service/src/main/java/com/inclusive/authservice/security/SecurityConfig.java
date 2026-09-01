package com.inclusive.authservice.security;

import com.inclusive.authservice.security.jwt.JwtAuthConverter;
import com.inclusive.authservice.security.tenant.TenantValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    FilterRegistrationBean<TenantValidationFilter> tenantFilterRegistration(
            TenantValidationFilter tenantFilter
    ) {
        // Keep the component available for injection, but execute it only in the security chain.
        FilterRegistrationBean<TenantValidationFilter> registration =
                new FilterRegistrationBean<>(tenantFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, TenantValidationFilter tenantFilter, JwtAuthConverter jwtAuthConverter
    ) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .addFilterAfter(tenantFilter, BearerTokenAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/mfa/**").authenticated()
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
