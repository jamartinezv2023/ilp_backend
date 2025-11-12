package com.inclusive.tenantservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * ConfiguraciÃ³n base del DataSource multi-tenant.
 *
 * Se inicializa un DataSource por defecto (tenant "public") y puede ampliarse
 * dinÃ¡micamente al registrar nuevas instituciones.
 *
 * Autor: JosÃ© Alfredo MartÃ­nez ValdÃ©s
 * Proyecto: Inclusive Learning Platform
 * MÃ³dulo: Tenant Service
 * AÃ±o: 2025
 */
@Configuration
public class MultiTenantDataSourceConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * Registra el DataSource principal (tenant "public").
     */
    @Bean
    public Map<String, DataSource> tenantDataSources() {
        Map<String, DataSource> result = new HashMap<>();

        DataSource defaultDataSource = DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(dbUrl)
                .username(dbUser)
                .password(dbPass)
                .build();

        // Tenant base (el esquema "public" de PostgreSQL)
        result.put("public", defaultDataSource);

        return result;
    }
}




