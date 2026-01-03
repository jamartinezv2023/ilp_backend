// Location: auth-service/src/main/java/com/inclusive/authservice/security/jwt/JwtConfig.java
package com.inclusive.authservice.security.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
}
