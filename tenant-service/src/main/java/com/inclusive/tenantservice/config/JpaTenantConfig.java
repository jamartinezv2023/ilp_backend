// Ruta: tenant-service/src/main/java/com/inclusive/tenantservice/config/JpaTenantConfig.java
package com.inclusive.tenantservice.config;

import com.inclusive.tenantservice.multitenancy.SchemaPerTenantConnectionProvider;
import com.inclusive.tenantservice.multitenancy.TenantSchemaResolver;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JpaTenantConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SchemaPerTenantConnectionProvider multiTenantConnectionProvider;

    @Autowired
    private TenantSchemaResolver tenantSchemaResolver;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        Map<String, Object> props = new HashMap<>();

        // ðŸ”§ ConfiguraciÃ³n multitenant (usando claves string explÃ­citas)
        props.put("hibernate.multiTenancy", "SCHEMA");
        props.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        props.put("hibernate.tenant_identifier_resolver", tenantSchemaResolver);
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", true);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setPackagesToScan("com.inclusive.tenantservice.tenant.entity");
        factoryBean.setJpaPropertyMap(props);
        factoryBean.setPersistenceUnitName("tenantServicePU");

        return factoryBean;
    }
}




