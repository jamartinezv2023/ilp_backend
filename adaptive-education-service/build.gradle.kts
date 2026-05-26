/*
 * adaptive-education-service/build.gradle.kts
 * Core adaptive engine: rules + recommendations (owns ONLY its adaptive data).
 */

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    java
}

group = "com.inclusive.platform"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {

    implementation(project(":common-events"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}






