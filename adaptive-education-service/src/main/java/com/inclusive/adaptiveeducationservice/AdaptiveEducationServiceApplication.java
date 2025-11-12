package com.inclusive.adaptiveeducationservice;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Inclusive Learning API", version = "1.0", description = "DocumentaciÃƒÂ³n automÃƒÂ¡tica generada con Swagger / OpenAPI"))

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdaptiveEducationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdaptiveEducationServiceApplication.class, args);
    }
}



