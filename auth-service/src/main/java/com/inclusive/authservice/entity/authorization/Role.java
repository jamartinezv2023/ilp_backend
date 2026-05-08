package com.inclusive.authservice.entity.authorization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "roles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "name"})
    }
)
public class Role {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    /* ====== Domain rules ====== */

    public void deactivate() {
        this.active = false;
        this.deletedAt = OffsetDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    /* ====== Getters & Setters ====== */

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }
}