/*
 * common-events/build.gradle.kts
 * Shared domain events for cross-service communication (contracts only).
 */

plugins {
    java
}

group = "edu.ilp"
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
    // Pure contracts module: keep it minimal, no Spring dependencies.
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
