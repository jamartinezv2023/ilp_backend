/*
 * Root build.gradle.kts
 * ILP Backend – Spring Boot Multi-module
 */

plugins {
    // Solo para versionado, NO se aplica al root
    id("org.springframework.boot") version "3.3.5" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    java
}

/**
 * Toolchain global
 */
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

/**
 * Configuración común a TODOS los proyectos
 */
allprojects {

    group = "edu.ilp"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

/**
 * Configuración común a TODOS los submódulos
 */
subprojects {

    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    /**
     * Dependencias comunes (forma SEGURA en Kotlin DSL)
     */
    dependencies {

        // Spring Boot BOM
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.5"))

        // Spring Cloud BOM (Gateway, LoadBalancer, etc.)
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.3"))

        testImplementation(platform("org.junit:junit-bom:5.10.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
