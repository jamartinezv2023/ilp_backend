package com.inclusive.tenantservice.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Proveedor de conexiones multi-tenant basado en esquemas.
 * Soporta un tenant por defecto ("public") como fallback.
 */
@Component
public class SchemaPerTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final long serialVersionUID = 1L;

    // Mapa dinÃ¡mico de tenants -> DataSource
    private final Map<String, DataSource> dataSources = new HashMap<>();

    public SchemaPerTenantConnectionProvider() {
        // ðŸ”¹ Cargar tenant por defecto al iniciar
        registerDefaultTenant();
    }

    private void registerDefaultTenant() {
        DataSource defaultDataSource = DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/inclusive_learning")
                .username("postgres")
                .password("postgres")
                .build();

        dataSources.put("public", defaultDataSource);
    }

    public void addTenantDataSource(String tenantId, DataSource dataSource) {
        dataSources.put(tenantId, dataSource);
    }

    @Override
    protected DataSource selectAnyDataSource() {
        // ðŸ”¹ Si no hay tenant, usar el "public"
        return dataSources.get("public");
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        if (tenantIdentifier == null || tenantIdentifier.isBlank()) {
            tenantIdentifier = "public";
        }

        DataSource dataSource = dataSources.get(tenantIdentifier);

        if (dataSource == null) {
            // ðŸ”¹ Fallback profesional
            dataSource = dataSources.get("public");
            if (dataSource == null) {
                throw new IllegalArgumentException("No existe DataSource ni fallback para tenant: " + tenantIdentifier);
            }
        }

        return dataSource;
    }
}




