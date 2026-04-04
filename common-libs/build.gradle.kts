/*
 * common-libs/build.gradle.kts
 * Módulo de librerías compartidas (DTOs, enums, contracts)
 */

plugins {
    `java-library`
}

group = "com.inclusive.platform"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    // API si otros módulos dependen directamente de estos DTOs
    api("jakarta.validation:jakarta.validation-api")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
