plugins {
    java
    id("net.serenity-bdd.serenity-gradle-plugin") version "4.2.16"
}

group = "com.inclusive.bdd"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {

    testImplementation("net.serenity-bdd:serenity-core:4.2.16")
    testImplementation("net.serenity-bdd:serenity-junit5:4.2.16")
    testImplementation("net.serenity-bdd:serenity-cucumber:4.2.16")
    testImplementation("net.serenity-bdd:serenity-screenplay:4.2.16")
    testImplementation("net.serenity-bdd:serenity-screenplay-rest:4.2.16")

    testImplementation(
        platform("io.cucumber:cucumber-bom:7.20.1")
    )
    testImplementation(
        "io.cucumber:cucumber-junit-platform-engine"
    )

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.platform:junit-platform-suite")

    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.named("aggregate"))
}
tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
