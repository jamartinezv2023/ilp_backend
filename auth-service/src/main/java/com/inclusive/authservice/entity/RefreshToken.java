package com.inclusive.authservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entidad RefreshToken persistida en PostgreSQL.
 * Pensada para:
 *  - Logout real (revocaciÃ³n de tokens)
 *  - RotaciÃ³n segura
 *  - AuditorÃ­a y anti-replay
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    /**
     * ID del usuario al que pertenece el token.
     * Apunta a la PK de la tabla de usuarios (ej: users.id).
     * Se usa como Long para no acoplarnos a una implementaciÃ³n concreta.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by_ip", length = 50)
    private String createdByIp;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_by_ip", length = 50)
    private String revokedByIp;

    /**
     * Token que reemplazÃ³ a este (para rotaciÃ³n segura).
     */
    @Column(name = "replaced_by_token", length = 512)
    private String replacedByToken;

    public RefreshToken() {
    }

    // ======================
    // Getters y setters
    // ======================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByIp() {
        return createdByIp;
    }

    public void setCreatedByIp(String createdByIp) {
        this.createdByIp = createdByIp;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRevokedByIp() {
        return revokedByIp;
    }

    public void setRevokedByIp(String revokedByIp) {
        this.revokedByIp = revokedByIp;
    }

    public String getReplacedByToken() {
        return replacedByToken;
    }

    public void setReplacedByToken(String replacedByToken) {
        this.replacedByToken = replacedByToken;
    }
}