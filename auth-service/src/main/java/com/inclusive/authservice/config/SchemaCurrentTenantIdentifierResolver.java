package com.inclusive.authservice.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component("schemaCurrentTenantIdentifierResolver")
public class SchemaCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public void clear() {
        currentTenant.remove();
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = currentTenant.get();
        return (tenant != null) ? tenant : "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}





