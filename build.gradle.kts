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
    id("org.sonarqube") version "5.0.0.4638"
    id("com.github.spotbugs") version "6.0.26" apply false
    id("info.solidsoft.pitest") version "1.15.0" apply false
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

    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.junit.platform") {
                useVersion("1.10.2")
            }

            if (requested.group == "org.junit.jupiter") {
                useVersion("5.10.2")
            }
        }
    }

    dependencies {
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.5"))
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.3"))

        testImplementation(platform("org.junit:junit-bom:5.10.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
        testImplementation("org.testcontainers:junit-jupiter:1.19.8")
        testImplementation("org.testcontainers:postgresql:1.19.8")

        testImplementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.6")

        testImplementation("org.postgresql:postgresql:42.7.4")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
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


    configurations.all {
        resolutionStrategy {
            force("com.github.docker-java:docker-java-core:3.3.6")
            force("com.github.docker-java:docker-java-api:3.3.6")
            force("com.github.docker-java:docker-java-transport:3.3.6")
        }
    }

    plugins.withType<JavaPlugin> {

        tasks.withType<Test> {
            useJUnitPlatform {
                excludeTags("integration")
            }
        }

        tasks.register<Test>("integrationTest") {

            group = "verification"

            description =
                "Runs integration tests tagged with @Tag(\"integration\")."

            val sourceSets =
                project.extensions.getByType<SourceSetContainer>()

            testClassesDirs =
                sourceSets["test"].output.classesDirs

            classpath =
                sourceSets["test"].runtimeClasspath

            useJUnitPlatform {
                includeTags("integration")
            }

            shouldRunAfter(tasks.named("test"))

            onlyIf {
                System.getenv("ENABLE_TESTCONTAINERS") == "true"
            }
        }
    }

    tasks.withType<JacocoReport> {
        dependsOn(tasks.withType<Test>())

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.withType<JacocoCoverageVerification> {
        dependsOn(tasks.withType<Test>())

        violationRules {
            rule {
                enabled = project.name != "auth-service"
                limit {
                    minimum = "0.20".toBigDecimal()
                }
            }
        }
    }

    tasks.named("check") {
        dependsOn(tasks.withType<JacocoCoverageVerification>())
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

sonar {
    properties {
        property("sonar.projectKey", "jamartinezv2023_ilp_backend")
        property("sonar.organization", "jamartinezv2023")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.java.binaries", "**/build/classes")
    }
}



subprojects {
    plugins.withId("jacoco") {
        tasks.withType<org.gradle.testing.jacoco.tasks.JacocoReport>().configureEach {
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }
        }
    }
}

