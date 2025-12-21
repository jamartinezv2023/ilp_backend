// Location: auth-service/src/main/java/com/inclusive/authservice/security/jwt/JwtProperties.java
package com.inclusive.authservice.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration properties.
 *
 * This class ONLY holds configuration.
 * No logic is allowed here.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret used to sign access tokens.
     */
    private String accessTokenSecret;

    /**
     * Access token expiration in milliseconds.
     */
    private long accessTokenExpirationMs;

    // getters & setters

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    public void setAccessTokenExpirationMs(long accessTokenExpirationMs) {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }
}
