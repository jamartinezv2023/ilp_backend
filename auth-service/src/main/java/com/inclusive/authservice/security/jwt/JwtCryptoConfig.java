// Location: auth-service/src/main/java/com/inclusive/authservice/security/jwt/JwtCryptoConfig.java
package com.inclusive.authservice.security.jwt;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtCryptoConfig {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Bean
    public JwtEncoder jwtEncoder(JwtProperties props) {

        SecretKey secretKey = new SecretKeySpec(
                props.getSecret().getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
        );

        JWK jwk = new OctetSequenceKey.Builder(secretKey)
                .algorithm(com.nimbusds.jose.JWSAlgorithm.HS256)
                .build();

        JWKSet jwkSet = new JWKSet(jwk);

        return new NimbusJwtEncoder(
                (jwkSelector, securityContext) -> jwkSelector.select(jwkSet)
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtProperties props) {

        SecretKey secretKey = new SecretKeySpec(
                props.getSecret().getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
        );

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
