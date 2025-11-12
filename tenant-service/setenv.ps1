# ===============================================================
# Inclusive Learning Platform - Tenant Service
# Script: setenv.ps1
# Autor: José Alfredo Martínez Valdés
# Fecha: 2025-11-03
# Descripción:
#   Configura JAVA_HOME, crea bases de datos de tenants,
#   carga datos iniciales y ejecuta el microservicio.
# ===============================================================

Write-Host "🚀 Iniciando entorno completo del Tenant Service..." -ForegroundColor Cyan

# --- 1️⃣ Detección del JDK instalado ---
$jdkPaths = @(
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.17+10",
    "C:\Program Files\Eclipse Adoptium\jdk-17",
    "C:\Program Files\AdoptOpenJDK\jdk-17",
    "C:\Program Files\Amazon Corretto\jdk17",
    "C:\Program Files\Java\jdk-17"
)
$foundJdk = $jdkPaths | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $foundJdk) {
    Write-Host "❌ No se encontró un JDK 17. Instálalo antes de continuar." -ForegroundColor Red
    exit 1
}
$env:JAVA_HOME = $foundJdk
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
Write-Host "✅ JAVA_HOME establecido en: $env:JAVA_HOME" -ForegroundColor Green

# --- 2️⃣ Validar herramientas ---
try {
    java -version
    mvn -v
    psql --version
} catch {
    Write-Host "⚠️ Verifica que PostgreSQL y Maven estén en el PATH." -ForegroundColor Yellow
    exit 1
}

# --- 3️⃣ Configurar conexión PostgreSQL ---
$pgUser = "postgres"
$pgPassword = "admin"
$pgHost = "localhost"
$pgPort = "5432"

$env:PGPASSWORD = $pgPassword

# --- 4️⃣ Crear bases de datos principales ---
$mainDbs = @("tenant_public", "tenant_medellin", "tenant_bogota", "tenant_cali", "tenant_barranquilla", "tenant_bucaramanga")

foreach ($db in $mainDbs) {
    Write-Host "🗄️ Creando base de datos: $db ..." -ForegroundColor Yellow
    psql -U $pgUser -h $pgHost -p $pgPort -c "CREATE DATABASE $db;" 2>$null
}

# --- 5️⃣ Ejecutar script SQL de tenants ---
$sqlFile = "src/main/resources/db/tenants_colombia.sql"
if (Test-Path $sqlFile) {
    Write-Host "📦 Cargando datos desde $sqlFile ..." -ForegroundColor Yellow
    psql -U $pgUser -h $pgHost -p $pgPort -d tenant_public -f $sqlFile
} else {
    Write-Host "⚠️ No se encontró el archivo tenants_colombia.sql. Asegúrate de colocarlo en src/main/resources/db/" -ForegroundColor Red
}

# --- 6️⃣ Compilar el proyecto ---
Write-Host "`n🧱 Compilando tenant-service..." -ForegroundColor Yellow
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error en la compilación." -ForegroundColor Red
    exit 1
}

# --- 7️⃣ Iniciar el microservicio ---
Write-Host "`n🌍 Iniciando Tenant Service en http://localhost:8082 ..." -ForegroundColor Cyan
mvn spring-boot:run
