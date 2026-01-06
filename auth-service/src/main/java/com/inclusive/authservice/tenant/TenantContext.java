// Location: auth-service/src/main/java/com/inclusive/authservice/tenant/TenantContext.java
package com.inclusive.authservice.tenant;

import java.util.UUID;

/**
 * Central tenant holder for the current request.
 * Backward compatible with existing services.
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    /* =========================
       Core setters / getters
       ========================= */

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    /* =========================
       Compatibility helpers
       ========================= */

    /**
     * Used by authorization services.
     * Fails fast if tenant is missing or invalid.
     */
    public static UUID getTenantIdAsUUID() {
        String tenant = getTenantId();

        if (tenant == null || tenant.isBlank()) {
            throw new IllegalStateException("Tenant not present in context");
        }

        try {
            return UUID.fromString(tenant);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("TenantId is not a valid UUID: " + tenant);
        }
    }
}
