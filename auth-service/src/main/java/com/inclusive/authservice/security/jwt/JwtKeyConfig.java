package com.inclusive.authservice.security.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtKeyConfig {

    @Bean
    public RSAKey rsaKey(
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey
    ) {

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("ilp-auth-rsa-key")
                .build();
    }

    @Bean
    public JWKSet jwkSet(RSAKey rsaKey) {
        return new JWKSet(rsaKey);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSet jwkSet) {

        return new NimbusJwtEncoder(
                (selector, context) -> selector.select(jwkSet)
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {

        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }
}
