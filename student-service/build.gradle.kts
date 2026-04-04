plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

group = "com.inclusive.platform"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // 🌐 API REST & VALIDATION
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation") // 👈 Necesario para validaciones de negocio

    // 🗄️ PERSISTENCE (JPA & PostgreSQL)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // 🛰️ MICROSERVICES (Eureka Client)
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // 🔗 SHARED CONTEXT
    implementation(project(":commons-service"))

    // 🌶️ LOMBOK (Optimizado sin versiones hardcoded)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // 🧪 TESTING
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
    
    // Soporte para pruebas con contextos de Spring Cloud
    testImplementation("org.springframework.cloud:spring-cloud-starter-bootstrap") 
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Configuración adicional para asegurar que el encoding sea siempre UTF-8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}