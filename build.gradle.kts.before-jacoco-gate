/*
 * Root build.gradle.kts
 * ILP Backend - Spring Boot Multi-module
 */

plugins {
    java
    jacoco
    checkstyle
    pmd

    id("org.springframework.boot") version "3.3.5" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    id("org.sonarqube") version "5.1.0.4882"
    id("com.github.spotbugs") version "6.0.26" apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

allprojects {
    group = "edu.ilp"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    dependencies {
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.5"))
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.3"))

        testImplementation(platform("org.junit:junit-bom:5.10.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    checkstyle {
        toolVersion = "10.17.0"
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    }

    pmd {
        toolVersion = "7.6.0"
        ruleSetFiles = files(rootProject.file("config/pmd/pmd-ruleset.xml"))
        ruleSets = emptyList()
        isIgnoreFailures = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<JacocoReport> {
        dependsOn(tasks.withType<Test>())

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.withType<Checkstyle> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    tasks.withType<Pmd> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
        ignoreFailures = true
        excludeFilter.set(rootProject.file("config/spotbugs/exclude.xml"))

        reports {
            create("html") {
                required.set(true)
            }
            create("xml") {
                required.set(true)
            }
        }
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "jamartinezv2023_ilp_backend")
        property("sonar.organization", "jamartinezv2023")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco/test/jacocoTestReport.xml")
    }
}


