package com.inclusive.authservice.tenant;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(String tenantId) {
        TENANT.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT.get();
    }

    public static UUID getTenantIdAsUUID() {
        String tenantId = TENANT.get();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID not set");
        }
        return UUID.fromString(tenantId);
    }

    public static void clear() {
        TENANT.remove();
    }
}