// src/main/java/com/inclusive/authservice/security/JwtTokenService.java
package com.inclusive.authservice.security;

import com.inclusive.authservice.model.Role;
import com.inclusive.authservice.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenService {

    @Value("${jwt.access-token-secret}")
    private String accessTokenSecret;

    @Value("${jwt.refresh-token-secret}")
    private String refreshTokenSecret;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private Key accessKey() {
        return Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Key refreshKey() {
        return Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    public AuthTokens generateTokens(User user) {
        Instant now = Instant.now();
        Instant accessExpiry = now.plusMillis(accessTokenExpirationMs);
        Instant refreshExpiry = now.plusMillis(refreshTokenExpirationMs);

        String accessToken = generateAccessToken(user, Date.from(now), Date.from(accessExpiry));
        String refreshToken = generateRefreshToken(user, Date.from(now), Date.from(refreshExpiry));

        return new AuthTokens(accessToken, refreshToken, accessExpiry, refreshExpiry);
    }

    private String generateAccessToken(User user, Date issuedAt, Date expiry) {
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("uid", user.getId())
                .claim("roles", roles)
                .setIssuedAt(issuedAt)
                .setExpiration(expiry)
                .signWith(accessKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(User user, Date issuedAt, Date expiry) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("uid", user.getId())
                .setIssuedAt(issuedAt)
                .setExpiration(expiry)
                .signWith(refreshKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Jws<Claims> parseAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey())
                .build()
                .parseClaimsJws(token);
    }

    private Jws<Claims> parseRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey())
                .build()
                .parseClaimsJws(token);
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parseAccessToken(token).getBody();
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseRefreshToken(token).getBody();
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getEmailFromAccessToken(String token) {
        return parseAccessToken(token).getBody().getSubject();
    }

    public String getEmailFromRefreshToken(String token) {
        return parseRefreshToken(token).getBody().getSubject();
    }
}
