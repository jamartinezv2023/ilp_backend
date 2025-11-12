<#
.SYNOPSIS
  Instalador automático de Terraform para Windows.

.DESCRIPTION
  Descarga la última versión estable de Terraform desde HashiCorp,
  la descomprime en C:\Terraform, la agrega al PATH del sistema
  y verifica su instalación final.

  Compatible con PowerShell 5.1+ y 7.x.
#>

Write-Host "==========================================="
Write-Host "  HASHICORP TERRAFORM AUTO-INSTALLER"
Write-Host "==========================================="

# --- Configuración ---
$terraformUrl = "https://releases.hashicorp.com/terraform/1.9.7/terraform_1.9.7_windows_amd64.zip"
$installPath = "C:\Terraform"
$zipPath = "$env:TEMP\terraform.zip"

# --- Crear carpeta destino si no existe ---
if (-Not (Test-Path $installPath)) {
    Write-Host "[INFO] Creando carpeta destino en $installPath..."
    New-Item -ItemType Directory -Force -Path $installPath | Out-Null
}

# --- Descargar binario ---
Write-Host "[INFO] Descargando Terraform desde HashiCorp..."
Invoke-WebRequest -Uri $terraformUrl -OutFile $zipPath -UseBasicParsing
Write-Host "[OK] Descarga completada: $zipPath"

# --- Extraer contenido ---
Write-Host "[INFO] Extrayendo binario..."
Expand-Archive -Force -Path $zipPath -DestinationPath $installPath
Remove-Item $zipPath -Force
Write-Host "[OK] Terraform extraído en $installPath"

# --- Agregar al PATH (solo si no existe) ---
$pathCurrent = [System.Environment]::GetEnvironmentVariable("PATH", "Machine")
if ($pathCurrent -notlike "*$installPath*") {
    Write-Host "[INFO] Agregando Terraform al PATH del sistema..."
    setx PATH "$pathCurrent;$installPath" /M | Out-Null
    Write-Host "[OK] PATH actualizado. Es necesario reiniciar la terminal."
} else {
    Write-Host "[OK] Terraform ya estaba en el PATH."
}

# --- Verificación final ---
Write-Host "`n[INFO] Verificando instalación..."
$terraformExe = Join-Path $installPath "terraform.exe"
if (Test-Path $terraformExe) {
    & $terraformExe -version
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n✅ Terraform instalado correctamente y funcional." -ForegroundColor Green
    } else {
        Write-Host "`n⚠️ Terraform está instalado pero no se pudo ejecutar correctamente." -ForegroundColor Yellow
    }
} else {
    Write-Host "`n❌ Error: No se encontró terraform.exe en $installPath" -ForegroundColor Red
}

Write-Host "`n--- FIN DEL INSTALADOR ---"
