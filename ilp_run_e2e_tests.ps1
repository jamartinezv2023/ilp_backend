# ============================================================
# File: ilp_run_e2e_tests.ps1
# Location: project root (inclusive-learning-platform-backend)
# Description: Ejecuta la batería completa de pruebas E2E
#              sobre el backend ILP.
# ============================================================

param(
    # URL base del gateway o backend central
    [string]$BaseUrl = "http://localhost:8080",

    # Si se indica este flag, NO se recrean las bases de datos
    [switch]$SkipDatabaseSetup,

    # Si se indica este flag, NO se hace clean install completo
    [switch]$SkipBuild
)

Write-Host "================================================="
Write-Host " ILP - Ejecución de batería E2E (enterprise)"
Write-Host "================================================="

# Obtener ruta del script para posicionarse en la raíz del repo
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# Validar que estamos en el pom raíz
if (-not (Test-Path ".\pom.xml")) {
    Write-Error "❌ No se encontró pom.xml en el directorio actual. Ejecuta este script desde la raíz del proyecto."
    exit 1
}

Write-Host "📂 Proyecto raíz: $scriptDir"
Write-Host "🌐 BaseUrl para pruebas E2E: $BaseUrl"
Write-Host ""

# 1) Opcional: crear bases de datos PostgreSQL
if (-not $SkipDatabaseSetup) {
    $dbScript = Join-Path $scriptDir "ilp_setup_postgres_databases.ps1"
    if (Test-Path $dbScript) {
        Write-Host "🗄  Ejecutando creación de bases de datos PostgreSQL..."
        & powershell -ExecutionPolicy Bypass -File $dbScript
        if ($LASTEXITCODE -ne 0) {
            Write-Error "❌ Falló la creación de bases de datos. Abortando ejecución E2E."
            exit 1
        }
    } else {
        Write-Host "⚠️  No se encontró ilp_setup_postgres_databases.ps1. Se omite la creación de BDs." -ForegroundColor Yellow
    }
} else {
    Write-Host "⏭  Omitiendo creación de bases de datos por parámetro -SkipDatabaseSetup."
}

Write-Host ""

# 2) Opcional: build completo del backend
if (-not $SkipBuild) {
    Write-Host "🛠  Ejecutando build completo: mvn clean install -DskipTests"
    mvn clean install -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Error "❌ Falló el build Maven. Revisa los errores anteriores."
        exit 1
    }
} else {
    Write-Host "⏭  Omitiendo build completo por parámetro -SkipBuild."
}

Write-Host ""

# 3) Ejecutar únicamente el módulo de pruebas E2E
Write-Host "🚀 Ejecutando pruebas E2E del módulo 'e2e-tests'..."
Write-Host ""

$mvnArgs = @(
    "-pl", "e2e-tests",
    "-DfailIfNoTests=false",
    "-Dilp.url=$BaseUrl",
    "test"
)

mvn @mvnArgs

if ($LASTEXITCODE -ne 0) {
    Write-Error "❌ Las pruebas E2E fallaron. Revisa los reportes en e2e-tests/target/surefire-reports."
    exit 1
}

Write-Host ""
Write-Host "✅ Pruebas E2E finalizadas correctamente." -ForegroundColor Green

# 4) Generar y abrir reportes Allure (si está configurado)
Write-Host ""
Write-Host "📊 Generando reporte Allure..."
mvn -pl e2e-tests allure:report

if ($LASTEXITCODE -eq 0) {
    $reportPath = Join-Path $scriptDir "e2e-tests\target\site\allure-maven-plugin\index.html"
    if (Test-Path $reportPath) {
        Write-Host "📁 Reporte Allure generado en:"
        Write-Host "    $reportPath"
        Write-Host "🌐 Abriendo reporte en el navegador predeterminado..."
        Start-Process $reportPath
    } else {
        Write-Host "⚠️  No se encontró el archivo HTML de reporte Allure. Revisa configuración del plugin." -ForegroundColor Yellow
    }
} else {
    Write-Host "⚠️  No se pudo generar el reporte Allure. Revisa configuración del plugin en el pom de e2e-tests." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "================================================="
Write-Host " Batería E2E ejecutada exitosamente."
Write-Host "================================================="
