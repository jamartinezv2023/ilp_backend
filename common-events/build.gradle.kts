/*
 * common-events/build.gradle.kts
 * Shared domain events for cross-service communication (contracts only).
 */

plugins {
    java
    id("info.solidsoft.pitest")
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
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


pitest {
    junit5PluginVersion.set("1.2.1")

    targetClasses.set(
        listOf(
            "com.inclusive.common.events.*",
            "com.inclusive.common.events.v1.*"
        )
    )

    targetTests.set(
        listOf(
            "com.inclusive.common.events.v1.*"
        )
    )

    threads.set(2)
    outputFormats.set(listOf("XML", "HTML"))
    timestampedReports.set(false)
    verbose.set(false)

    mutationThreshold.set(10)

    excludedMethods.set(
        listOf(
            "hashCode",
            "equals",
            "toString"
        )
    )
}
