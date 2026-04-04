// UbicaciÃ³n:
// src/main/java/com/inclusive/authservice/config/FilterConfig.java

package com.inclusive.authservice.config;

import com.inclusive.authservice.multitenancy.filter.TenantHeaderFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TenantHeaderFilter> tenantHeaderFilter() {
        FilterRegistrationBean<TenantHeaderFilter> registration =
            new FilterRegistrationBean<>();

        registration.setFilter(new TenantHeaderFilter());

        // ðŸ”¥ SOLO para APIs protegidas
        registration.addUrlPatterns("/api/*");

        // ðŸ” Se ejecuta despuÃ©s de filtros internos
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);

        return registration;
    }
}
