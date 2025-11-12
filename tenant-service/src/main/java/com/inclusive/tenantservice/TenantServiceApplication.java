package com.inclusive.tenantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Clase principal del microservicio Tenant Service.
 * Forzamos el escaneo explÃ­cito de entidades y repositorios.
 */
@SpringBootApplication(scanBasePackages = "com.inclusive.tenantservice")
@EnableJpaRepositories(basePackages = "com.inclusive.tenantservice.tenant.repository")
@EntityScan(basePackages = "com.inclusive.tenantservice.tenant.entity")
public class TenantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TenantServiceApplication.class, args);
    }
}




