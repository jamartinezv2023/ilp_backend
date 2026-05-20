// Location: auth-service/src/main/java/com/inclusive/authservice/entity/UserAccount.java
package com.inclusive.authservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "user_accounts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "email"})
)
@Getter
public class UserAccount {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected UserAccount() {
        // JPA only
    }

    public UserAccount(
            UUID id,
            UUID tenantId,
            String email,
            String passwordHash,
            boolean enabled,
            boolean mfaEnabled,
            String mfaSecret,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void prepareMfaEnrollment(String mfaSecret) {
        this.mfaSecret = mfaSecret;
        this.mfaEnabled = false;
        this.updatedAt = Instant.now();
    }

    public void enableMfa() {
        this.mfaEnabled = true;
        this.updatedAt = Instant.now();
    }

    public boolean hasMfaSecret() {
        return this.mfaSecret != null && !this.mfaSecret.isBlank();
    }

}
