// Location: auth-service/src/main/java/com/inclusive/authservice/entity/UserAccount.java
package com.inclusive.authservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "user_accounts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "email"})
    }
)
public class UserAccount {

    // =========================
    // Primary Key
    // =========================
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // =========================
    // Multitenancy
    // =========================
    @Column(name = "tenant_id", nullable = false, length = 64)
    private String tenantId;

    // =========================
    // Identity
    // =========================
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    // =========================
    // MFA (Multi-Factor Auth)
    // =========================
    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled = false;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    // =========================
    // Learning Styles (future IA)
    // =========================
    @Column(name = "learning_style_kolb")
    private String learningStyleKolb;

    @Column(name = "learning_style_felder")
    private String learningStyleFelder;

    @Column(name = "kuder_preferences")
    private String kuderPreferences;

    // =========================
    // Auditing
    // =========================
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // =========================
    // JPA Hooks
    // =========================
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // =========================
    // Getters and Setters
    // =========================
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public String getLearningStyleKolb() {
        return learningStyleKolb;
    }

    public void setLearningStyleKolb(String learningStyleKolb) {
        this.learningStyleKolb = learningStyleKolb;
    }

    public String getLearningStyleFelder() {
        return learningStyleFelder;
    }

    public void setLearningStyleFelder(String learningStyleFelder) {
        this.learningStyleFelder = learningStyleFelder;
    }

    public String getKuderPreferences() {
        return kuderPreferences;
    }

    public void setKuderPreferences(String kuderPreferences) {
        this.kuderPreferences = kuderPreferences;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
