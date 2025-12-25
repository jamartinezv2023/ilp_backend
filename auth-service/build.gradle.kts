plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    java
}

group = "com.inclusive"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // -------------------------------------------------------------------------
    // Spring Boot Core
    // -------------------------------------------------------------------------
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // -------------------------------------------------------------------------
    // Security
    // -------------------------------------------------------------------------
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT / OAuth2 Resource Server (OBLIGATORIO para Jwt, JwtDecoder, etc.)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // -------------------------------------------------------------------------
    // Lombok
    // -------------------------------------------------------------------------
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // -------------------------------------------------------------------------
    // Database
    // -------------------------------------------------------------------------
    runtimeOnly("org.postgresql:postgresql")

    // Base de datos en memoria solo para tests
    testImplementation("com.h2database:h2")

    // -------------------------------------------------------------------------
    // Mapping
    // -------------------------------------------------------------------------
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-codec:commons-codec:1.17.0")

    // -------------------------------------------------------------------------
    // Testing
    // -------------------------------------------------------------------------
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

