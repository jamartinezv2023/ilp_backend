# ------------------------------------------------------------------
# Archivo: fix-java-maven.ps1
# Propósito: Configurar JAVA_HOME y compilar el proyecto Spring Boot en PowerShell
# Autor: José Alfredo Martínez Valdés
# Fecha: 2025-11-03
# ------------------------------------------------------------------

Write-Host "🔍 Detectando instalación de Java..." -ForegroundColor Cyan
$javaPath = (Get-Command java | Select-Object -ExpandProperty Source)
if (-not $javaPath) {
    Write-Host "❌ Java no se encuentra en el PATH. Instálalo o verifica la ruta." -ForegroundColor Red
    exit
}

# Obtener carpeta padre de bin
$javaHome = Split-Path (Split-Path $javaPath)
$env:JAVA_HOME = $javaHome
$env:Path = "$env:JAVA_HOME\bin;C:\Program Files\apache-maven-3.9.11\bin;$env:Path"

Write-Host "----------------------------------------"
Write-Host "✅ JAVA_HOME configurado en:" $env:JAVA_HOME -ForegroundColor Green
Write-Host "----------------------------------------"
java -version
Write-Host "----------------------------------------"
mvn -v
Write-Host "----------------------------------------"

# Ir al módulo tenant-service
Set-Location "C:\Users\iesaf\OneDrive\Documentos\TEAC2025-26\Reconstruccion_19102025\inclusive-learning-platform-backend\tenant-service"

# Ejecutar compilación
Write-Host "🚀 Ejecutando 'mvn clean package -DskipTests'..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilación completada correctamente." -ForegroundColor Green
} else {
    Write-Host "❌ Error durante la compilación. Revisa el log anterior." -ForegroundColor Red
}
