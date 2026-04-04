/*
 * notification-service/build.gradle.kts
 */

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

dependencies {

    // 🔗 Eventos compartidos
    implementation(project(":common-events"))

    // 🌱 Spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 🧪 Base de datos temporal
    runtimeOnly("com.h2database:h2")

    // 🧪 Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
