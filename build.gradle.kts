/*
 * Root build.gradle.kts
 * ILP Backend - Spring Boot Multi-module
 */

plugins {
    java
    jacoco

    id("org.springframework.boot") version "3.3.5" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false

    id("org.sonarqube") version "5.1.0.4882"
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

    tasks.withType<Test> {

        useJUnitPlatform()

        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.withType<JacocoReport> {

        dependsOn(tasks.withType<Test>())

        reports {

            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }
}

sonarqube {

    properties {

        property("sonar.projectKey", "TU_PROJECT_KEY")
        property("sonar.projectName", "ILP Backend")

        property("sonar.organization", "TU_ORGANIZATION")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")

        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "**/build/reports/jacoco/test/jacocoTestReport.xml"
        )
    }
}

