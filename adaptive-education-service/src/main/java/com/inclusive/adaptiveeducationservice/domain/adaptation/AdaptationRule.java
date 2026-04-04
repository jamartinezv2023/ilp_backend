package com.inclusive.adaptiveeducationservice.domain.adaptation;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Owned by adaptive-education-service.
 * Represents an adaptation rule that can be configured per tenant.
 *
 * NOTE: This service does NOT own Student or Assessment data.
 * It only owns adaptive rules and derived recommendations.
 */
@Entity
@Table(name = "adaptation_rules")
public class AdaptationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tenant scope. Use String to avoid coupling to UUID type across services.
     */
    @Column(nullable = false)
    private String tenantId;

    /**
     * High-level rule name (for admin / audit).
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Simple condition expression (MVP).
     * Example:
     *   "TYPE=KOLB && SCORE<60"
     */
    @Column(nullable = false, length = 500)
    private String conditionExpression;

    /**
     * Action payload (MVP).
     * Example:
     *   "RECOMMEND:REINFORCEMENT_PACK_A"
     */
    @Column(nullable = false, length = 500)
    private String actionExpression;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected AdaptationRule() {}

    public AdaptationRule(String tenantId, String name, String conditionExpression, String actionExpression) {
        this.tenantId = tenantId;
        this.name = name;
        this.conditionExpression = conditionExpression;
        this.actionExpression = actionExpression;
        this.enabled = true;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getConditionExpression() { return conditionExpression; }
    public String getActionExpression() { return actionExpression; }
    public boolean isEnabled() { return enabled; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public void setName(String name) { this.name = name; }
    public void setConditionExpression(String conditionExpression) { this.conditionExpression = conditionExpression; }
    public void setActionExpression(String actionExpression) { this.actionExpression = actionExpression; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
