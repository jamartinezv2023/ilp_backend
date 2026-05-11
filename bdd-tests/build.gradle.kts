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

    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.18.1")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.named("aggregate"))
}
