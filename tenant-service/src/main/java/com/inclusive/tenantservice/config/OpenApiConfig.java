// ðŸ“ tenant-service/src/main/java/com/inclusive/tenant/config/OpenApiConfig.java
package com.inclusive.tenant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tenant Service API - Inclusive Learning Platform")
                        .version("1.0.0")
                        .description("API para la gestiÃ³n multi-tenant de instituciones educativas dentro de la plataforma inclusiva.")
                        .contact(new Contact()
                                .name("JosÃ© Alfredo MartÃ­nez ValdÃ©s")
                                .email("jose.martinez7@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}




