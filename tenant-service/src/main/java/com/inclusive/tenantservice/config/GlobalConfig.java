package com.inclusive.tenantservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.http.MediaType;

import java.util.Collections;

/**
 * ConfiguraciÃ³n global UTF-8 para toda la plataforma Inclusive Learning.
 *
 * Este mÃ³dulo garantiza la codificaciÃ³n de caracteres en UTF-8 en todos los
 * microservicios del ecosistema (usuarios, roles, autenticaciÃ³n, cuestionarios, etc.),
 * asegurando la correcta visualizaciÃ³n de tildes, eÃ±es y caracteres especiales
 * en las respuestas HTML, JSON y XML.
 *
 * Autor: JosÃ© Alfredo MartÃ­nez ValdÃ©s
 * Proyecto: Inclusive Learning Platform
 * AÃ±o: 2025
 */
@Configuration
public class GlobalConfig implements WebMvcConfigurer {

    /**
     * Define el filtro global de codificaciÃ³n UTF-8.
     */
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    /**
     * Configura la negociaciÃ³n de contenido predeterminada para UTF-8.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
        configurer.mediaTypes(Collections.singletonMap("html", MediaType.TEXT_HTML));
    }
}




