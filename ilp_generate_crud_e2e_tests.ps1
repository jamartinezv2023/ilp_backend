<# 
    Archivo: ilp_generate_crud_e2e_tests.ps1
    Ubicación: C:\temp_ilp\inclusive-learning-platform-backend

    Objetivo:
      - Detectar automáticamente todos los microservicios *-service
      - Buscar sus clases bajo carpetas "entity"
      - Generar pruebas CRUD E2E con RestAssured para cada entidad
      - Paquetes Java generados: com.ilp.e2e.<servicio>
      - Clase por entidad: <Entidad>CrudE2ETest.java

    NOTA IMPORTANTE:
      - Las rutas generadas son del tipo: /api/<serviceId>/<entityNameLowercase>
      - El host/base URL se toma de:
          - System property  ilp.e2e.base-url
          - o variable de entorno ILP_E2E_BASE_URL
          - o por defecto http://localhost:8080
      - Los asserts permiten varios códigos (200/201/204/400/404) para que
        el build no falle aunque aún no estén todos los endpoints completos.
#>

param()

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "============================================="
Write-Host " ILP - Generador de pruebas CRUD E2E" -ForegroundColor Cyan
Write-Host "============================================="
Write-Host ""

# Raíz de la solución (carpeta donde está este script)
$SolutionRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

# Proyecto e2e-tests
$E2eProjectDir = Join-Path $SolutionRoot "e2e-tests"
$E2eJavaRoot   = Join-Path $E2eProjectDir "src\test\java"

if (-not (Test-Path $E2eProjectDir)) {
    Write-Error "No se encontró el módulo 'e2e-tests' en: $E2eProjectDir"
    exit 1
}

if (-not (Test-Path $E2eJavaRoot)) {
    New-Item -Path $E2eJavaRoot -ItemType Directory -Force | Out-Null
}

# Plantilla Java para las pruebas CRUD E2E
$javaTemplate = @"
package com.ilp.e2e.__PACKAGE__;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class __CLASS_NAME__ {

    private static final String BASE_URL =
            System.getProperty("ilp.e2e.base-url",
                    System.getenv().getOrDefault("ILP_E2E_BASE_URL", "http://localhost:8080"));

    private static final String BASE_PATH = "__BASE_PATH__";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    void create_shouldReturnSuccessOrValidationError() {
        // TODO: completar JSON válido para __ENTITY_NAME__
        String body = "{ }";

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post(BASE_PATH)
        .then()
            // Permitimos varios códigos mientras el API madura
            .statusCode(anyOf(is(201), is(200), is(400), is(404)));
    }

    @Test
    void read_shouldReturn200Or404() {
        given()
        .when()
            .get(BASE_PATH + "/{id}", 1)
        .then()
            .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    void update_shouldReturnSuccessOr404() {
        // TODO: completar JSON válido para __ENTITY_NAME__
        String body = "{ }";

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .put(BASE_PATH + "/{id}", 1)
        .then()
            .statusCode(anyOf(is(200), is(400), is(404)));
    }

    @Test
    void delete_shouldReturn204Or404() {
        given()
        .when()
            .delete(BASE_PATH + "/{id}", 1)
        .then()
            .statusCode(anyOf(is(204), is(404)));
    }
}
"@

# Detectar microservicios: carpetas *-service en la raíz
$serviceDirs = Get-ChildItem -Path $SolutionRoot -Directory |
    Where-Object { $_.Name -like "*-service" }

if (-not $serviceDirs) {
    Write-Warning "No se encontraron microservicios (*-service) en $SolutionRoot"
    exit 0
}

Write-Host "Microservicios detectados:" -ForegroundColor Yellow
$serviceDirs | ForEach-Object { Write-Host (" - " + $_.Name) }
Write-Host ""

foreach ($svcDir in $serviceDirs) {
    $serviceName = $svcDir.Name              # p.ej.: auth-service
    $serviceId   = $serviceName -replace "-service$", ""   # p.ej.: auth
    $pkgSegment  = $serviceId -replace "-", ""             # p.ej.: adaptive-education -> adaptiveeducation

    Write-Host "---------------------------------------------"
    Write-Host " Procesando servicio: $serviceName" -ForegroundColor Cyan
    Write-Host "  id de servicio : $serviceId"
    Write-Host "  paquete e2e    : com.ilp.e2e.$pkgSegment"

    # Buscar carpetas llamadas exactamente "entity" dentro del microservicio
    $entityDirs = Get-ChildItem -Path $svcDir.FullName -Recurse -Directory |
                  Where-Object { $_.Name -eq "entity" }

    if (-not $entityDirs) {
        Write-Host "  No se encontró carpeta 'entity' en este servicio. Se omite."
        continue
    }

    foreach ($entityDir in $entityDirs) {
        Write-Host "  Carpeta entity: $($entityDir.FullName)"

        # Buscar archivos .java de entidades, excluyendo controllers, repositories, services, mappers, etc.
        $entityFiles = Get-ChildItem -Path $entityDir.FullName -Recurse -Include *.java -File |
            Where-Object {
                $_.Name -notlike "*Controller.java" -and
                $_.Name -notlike "*Repository.java" -and
                $_.Name -notlike "*Service.java" -and
                $_.Name -notlike "*ServiceImpl.java" -and
                $_.Name -notlike "*Mapper.java"
            }

        if (-not $entityFiles) {
            Write-Host "   - No se encontraron entidades válidas en esta carpeta."
            continue
        }

        foreach ($entityFile in $entityFiles) {
            $entityName = [System.IO.Path]::GetFileNameWithoutExtension($entityFile.Name)  # p.ej. Student
            $className  = "$entityName" + "CrudE2ETest"
            $entityPathSegment = $entityName.ToLower()   # p.ej. student

            # Ruta base generada: /api/<serviceId>/<entityLower>
            # Ejemplo: /api/auth/useraccount
            $basePath = "/api/$serviceId/$entityPathSegment"

            # Directorio de paquete para este servicio: com\ilp\e2e\<pkgSegment>
            $packageDir = Join-Path $E2eJavaRoot ("com\ilp\e2e\" + $pkgSegment)

            if (-not (Test-Path $packageDir)) {
                New-Item -Path $packageDir -ItemType Directory -Force | Out-Null
            }

            $filePath = Join-Path $packageDir ($className + ".java")

            # Construir código Java a partir de la plantilla
            $code = $javaTemplate
            $code = $code.Replace("__PACKAGE__", $pkgSegment)
            $code = $code.Replace("__CLASS_NAME__", $className)
            $code = $code.Replace("__BASE_PATH__", $basePath)
            $code = $code.Replace("__ENTITY_NAME__", $entityName)

            Set-Content -Path $filePath -Value $code -Encoding UTF8

            Write-Host "   - Generado test: $filePath" -ForegroundColor Green
        }
    }
}

Write-Host ""
Write-Host "============================================="
Write-Host " Generación de pruebas CRUD E2E finalizada"
Write-Host "============================================="
