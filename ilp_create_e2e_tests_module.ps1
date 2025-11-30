<#
===============================================
 ILP - Crear módulo E2E enterprise: e2e-tests
 ===============================================
#>

Write-Host "==============================================="
Write-Host " ILP - Crear módulo E2E enterprise: e2e-tests"
Write-Host "==============================================="

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$moduleName = "e2e-tests"
$modulePath = Join-Path $root $moduleName

#---------------------------------------------
# 1. VALIDAR SI EXISTE
#---------------------------------------------
if (Test-Path $modulePath) {
    Write-Host "[AVISO] El módulo '$moduleName' ya existe en: $modulePath" -ForegroundColor Yellow
    Write-Host "      - Elimínalo manualmente o con:"
    Write-Host "        Remove-Item -Recurse -Force .\e2e-tests"
    exit
}

#---------------------------------------------
# 2. CREAR ESTRUCTURA
#---------------------------------------------
Write-Host "Creando estructura de directorios..."
New-Item -ItemType Directory -Path $modulePath | Out-Null
New-Item -ItemType Directory -Path "$modulePath\src\test\java\com\ilp\e2e" -Force | Out-Null
New-Item -ItemType Directory -Path "$modulePath\src\test\resources" -Force | Out-Null

#---------------------------------------------
# 3. CREAR POM.XML DE E2E-TESTS (UTF-8)
#---------------------------------------------
$pomContent = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.inclusive.platform</groupId>
        <artifactId>inclusive-learning-platform-backend</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>e2e-tests</artifactId>
    <packaging>jar</packaging>

    <dependencies>

        <!-- REST ASSURED -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>5.4.0</version>
            <scope>test</scope>
        </dependency>

        <!-- JSON Assertions -->
        <dependency>
            <groupId>org.skyscreamer</groupId>
            <artifactId>jsonassert</artifactId>
            <version>1.5.1</version>
            <scope>test</scope>
        </dependency>

        <!-- JUnit -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>

        <!-- SPRING BOOT TESTCLIENT -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
"@

Set-Content -Path "$modulePath\pom.xml" -Value $pomContent -Encoding UTF8

#---------------------------------------------
# 4. CREAR UN TEST BASE
#---------------------------------------------
$testClass = @"
package com.ilp.e2e;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;

public class SmokeTest {

    @Test
    void backendIsOnline() {
        given()
            .when().get("http://localhost:8080/actuator/health")
            .then()
            .statusCode(200);
    }
}
"@

Set-Content -Path "$modulePath\src\test\java\com\ilp\e2e\SmokeTest.java" -Value $testClass -Encoding UTF8

#---------------------------------------------
# 5. AGREGAR EL MÓDULO AL POM RAIZ
#---------------------------------------------
Write-Host "Actualizando pom.xml raíz..."

$rootPomPath = Join-Path $root "pom.xml"
$rootPom = Get-Content $rootPomPath -Raw

if ($rootPom -notmatch "<module>e2e-tests</module>") {
    $rootPom = $rootPom -replace "(?s)(<modules>.*?)(</modules>)", "`$1    <module>e2e-tests</module>`n`$2"
    Set-Content -Path $rootPomPath -Value $rootPom -Encoding UTF8
    Write-Host " -> Módulo e2e-tests agregado al pom raíz."
} else {
    Write-Host " -> El pom raíz ya contenía el módulo."
}

Write-Host "==============================================="
Write-Host " Módulo e2e-tests creado correctamente"
Write-Host "==============================================="
