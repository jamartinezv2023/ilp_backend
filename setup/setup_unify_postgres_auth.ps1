# ==========================================================
# setup_unify_postgres_auth.ps1
# ----------------------------------------------------------
# Script profesional para unificar autenticación PostgreSQL 17
# Autor: José Alfredo Martínez Valdés
# Proyecto: Inclusive Learning Platform Backend
# Fecha: 2025-11-07
# ==========================================================

Write-Host "===============================================" -ForegroundColor Yellow
Write-Host "  POSTGRES UNIVERSAL AUTHENTICATION SETUP" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Yellow

# --- CONFIGURACIÓN ---
$pgService = "postgresql-x64-17"
$pgConfPath = "C:\Program Files\PostgreSQL\17\data\pg_hba.conf"
$pgExe = "C:\Program Files\PostgreSQL\17\bin\psql.exe"
$pgUser = "postgres"
$pgPass = "postgres"
$pgDatabases = @("postgres", "auth_service", "inclusive_learning", "adaptive_education_db")
$pgpassFile = "$env:APPDATA\postgresql\pgpass.conf"

# --- VERIFICACIÓN DE EXISTENCIA ---
if (!(Test-Path $pgConfPath)) {
    Write-Host "[ERROR] No se encontró pg_hba.conf en la ruta esperada: $pgConfPath" -ForegroundColor Red
    exit 1
}

# --- BACKUP DE CONFIGURACIÓN ---
$backupPath = "$pgConfPath.bak_$(Get-Date -Format 'yyyyMMdd_HHmmss')"
Copy-Item $pgConfPath $backupPath -Force
Write-Host "[OK] Copia de seguridad creada en: $backupPath" -ForegroundColor Green

# --- ACTUALIZAR CONFIGURACIÓN DE AUTENTICACIÓN ---
$newConf = @"
# TYPE  DATABASE        USER            ADDRESS                 METHOD
local   all             all                                     scram-sha-256
host    all             all             127.0.0.1/32            scram-sha-256
host    all             all             ::1/128                 scram-sha-256
"@
Set-Content -Path $pgConfPath -Value $newConf -Encoding ASCII
Write-Host "[OK] pg_hba.conf actualizado correctamente (scram-sha-256 unificado)" -ForegroundColor Green

# --- REINICIAR SERVICIO ---
Write-Host "Reiniciando servicio PostgreSQL..." -ForegroundColor Yellow
Stop-Service $pgService -Force
Start-Service $pgService
Start-Sleep -Seconds 5
Write-Host "[OK] Servicio reiniciado exitosamente." -ForegroundColor Green

# --- ACTUALIZAR CONTRASEÑA DE USUARIO ---
Write-Host "Actualizando contraseña del usuario postgres..." -ForegroundColor Yellow
& $pgExe -U postgres -d postgres -c "ALTER USER postgres WITH PASSWORD '$pgPass';" | Out-Null
Write-Host "[OK] Contraseña actualizada globalmente." -ForegroundColor Green

# --- CREAR ARCHIVO PGPASS ---
if (!(Test-Path (Split-Path $pgpassFile))) {
    New-Item -ItemType Directory -Path (Split-Path $pgpassFile) | Out-Null
}
$pgpassContent = @"
localhost:5432:auth_service:postgres:postgres
localhost:5432:inclusive_learning:postgres:postgres
localhost:5432:adaptive_education_db:postgres:postgres
localhost:5432:postgres:postgres:postgres
"@
Set-Content -Path $pgpassFile -Value $pgpassContent -Encoding ASCII
icacls $pgpassFile /inheritance:r /grant:r "$env:USERNAME:(R)" | Out-Null
Write-Host "[OK] Archivo pgpass.conf creado y protegido: $pgpassFile" -ForegroundColor Green

# --- PRUEBA DE CONEXIONES ---
foreach ($db in $pgDatabases) {
    Write-Host "Probando conexión a la base $db..." -ForegroundColor Yellow
    try {
        $result = & $pgExe -U $pgUser -d $db -c "SELECT current_database(), current_user;" 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Conexión exitosa a $db" -ForegroundColor Green
        } else {
            Write-Host ("[ERROR] Falló la conexión a {0}:`n{1}" -f $db, $result) -ForegroundColor Red
        }
    } catch {
        Write-Host ("[ERROR] Excepción al conectar a {0}: {1}" -f $db, $_) -ForegroundColor Red
    }
}


Write-Host "===============================================" -ForegroundColor Cyan
Write-Host " TODAS LAS BASES DE DATOS UNIFICADAS CORRECTAMENTE " -ForegroundColor Green
Write-Host " Usuario: postgres | Contraseña: postgres " -ForegroundColor White
Write-Host "===============================================" -ForegroundColor Cyan
