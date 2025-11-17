package com.inclusive.authservice.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI inclusiveOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inclusive Auth Service API")
                        .description("API for Inclusive Education System with Adaptive Learning and AI")
                        .version("1.0.0"));
    }
}




