package com.inclusive.authservice.entity.authorization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "user_roles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "role_id", "tenant_id"})
    }
)
public class UserRole {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(nullable = false)
    private boolean active;

    protected UserRole() {
        // JPA only
    }

    /* ===== Domain constructor ===== */

    public UserRole(UUID userId, UUID roleId, UUID tenantId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.assignedAt = LocalDateTime.now();
        this.active = true;
    }

    /* ===== Domain behavior ===== */

    public void revoke() {
        this.active = false;
        this.revokedAt = LocalDateTime.now();
    }

    public boolean isEffective() {
        return active && revokedAt == null;
    }

    /* ===== Getters ===== */

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getRoleId() { return roleId; }
    public UUID getTenantId() { return tenantId; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public boolean isActive() { return active; }
}
