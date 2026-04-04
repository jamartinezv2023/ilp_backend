/*
 * assessment-service/build.gradle.kts
 */

plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":common-events"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation(project(":common-libs"))


    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
