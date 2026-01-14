plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    java
}

group = "com.inclusive.platform"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {

    // 🌐 Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // 🔐 Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // 🔑 OAuth2 Resource Server (validación JWT)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // 🔐 JOSE / JWT (EMISIÓN DE TOKENS ✅ OBLIGATORIO)
    implementation("org.springframework.security:spring-security-oauth2-jose")

    // 🗄️ JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ✅ Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // 🧠 Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // 🗺️ MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // 🛢️ H2
    runtimeOnly("com.h2database:h2")

    // 🧪 Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
