package com.inclusive.studentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
// 🎯 1. Escanea SOLO los componentes (Services, Controllers) de este microservicio
@ComponentScan(basePackages = "com.inclusive.studentservice")

// 🧬 2. Escanea las ENTIDADES (Tablas) en ambos proyectos (local y commons)
@EntityScan(basePackages = {
    "com.inclusive.studentservice.domain", 
    "com.inclusive.common.domain"
})

// 📂 3. Escanea los REPOSITORIOS (Interfaces JPA) locales
@EnableJpaRepositories(basePackages = "com.inclusive.studentservice.repository")
public class StudentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}