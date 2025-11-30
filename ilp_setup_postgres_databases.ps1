# ============================================================
# ILP - Inclusive Learning Platform
# Script para crear todas las bases de datos PostgreSQL
# Sin BOM, sin UTF-8 extendido, sin caracteres unicode
# ============================================================

Write-Host "============================================="
Write-Host " ILP - Creacion de Bases de Datos PostgreSQL "
Write-Host "============================================="

# Configuracion
$Server = "localhost"
$Port   = "5432"
$PgUser = "postgres"
$PgPass = "postgres"

# Lista de bases de datos requeridas
$Databases = @(
    "ilp_auth_db",
    "ilp_commons_db",
    "ilp_adaptive_db",
    "ilp_assessment_db",
    "ilp_notification_db",
    "ilp_monitoring_db",
    "ilp_reports_db",
    "ilp_tenant_db",
    "ilp_gateway_db",
    "ilp_e2e_db"
)

# Verificar si psql esta disponible
Write-Host ""
Write-Host "Verificando instalacion de psql..."

$psqlPath = Get-Command "psql.exe" -ErrorAction SilentlyContinue
if (-not $psqlPath) {
    Write-Host "ERROR: No se encontro psql.exe en el PATH."
    exit 1
}

Write-Host "psql encontrado en: $($psqlPath.Source)"

# Validar conexion
Write-Host ""
Write-Host "Verificando conexion a PostgreSQL..."

$env:PGPASSWORD = $PgPass
& psql -U $PgUser -h $Server -p $Port -c "\q" 2>$null

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: No es posible conectarse a PostgreSQL."
    exit 1
}

Write-Host "Conexion OK."

# Funcion para crear bases de datos
function Create-ILPDatabase {
    param([string]$DbName)

    Write-Host ""
    Write-Host "Procesando base de datos: $DbName"

    $query = "SELECT 1 FROM pg_database WHERE datname='$DbName';"
    $exists = & psql -U $PgUser -h $Server -p $Port -tAc $query

    if ($exists -eq "1") {
        Write-Host "La base de datos ya existe: $DbName"
    }
    else {
        Write-Host "Creando base de datos..."

        $createCmd = "CREATE DATABASE `"$DbName`";"
        & psql -U $PgUser -h $Server -p $Port -c $createCmd

        if ($LASTEXITCODE -eq 0) {
            Write-Host "Base de datos creada correctamente."
        }
        else {
            Write-Host "ERROR al crear la base de datos: $DbName"
        }
    }
}

# Ejecutar creacion
Write-Host ""
Write-Host "============================================="
Write-Host " Creando / Verificando bases de datos ILP"
Write-Host "============================================="

foreach ($db in $Databases) {
    Create-ILPDatabase -DbName $db
}

Write-Host ""
Write-Host "============================================="
Write-Host " FINALIZADO: Todas las bases estan configuradas"
Write-Host "============================================="
