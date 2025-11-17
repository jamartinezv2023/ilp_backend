package com.inclusive.authservice.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Hibernate 6+ compatible MultiTenantConnectionProvider implementation.
 * Provides a physical connection for each tenant schema dynamically.
 */
@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<Object> {

    private static final long serialVersionUID = 1L;

    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    @Autowired
    private DataSource defaultDataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return defaultDataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        try {
            String schema = tenantIdentifier != null ? tenantIdentifier.toString() : "public";
            connection.setSchema(schema);
        } catch (SQLException ex) {
            throw new SQLException("Could not set schema for tenant: " + tenantIdentifier, ex);
        }
        return connection;
    }

    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        try {
            connection.setSchema("public");
        } catch (SQLException ex) {
            // Ignorar errores al restaurar el esquema
        }
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return MultiTenantConnectionProvider.class.equals(unwrapType) ||
               SchemaMultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return unwrapType.cast(this);
        } else {
            throw new IllegalArgumentException("Cannot unwrap to " + unwrapType);
        }
    }
}




