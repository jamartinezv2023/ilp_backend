Write-Host "==============================================="
Write-Host " ILP – FIX JAVA SOURCES (UTF-8 + CLEAN STRINGS)"
Write-Host "==============================================="

$root = Get-Location

# Servicios a corregir
$services = @(
    "auth-service",
    "tenant-service",
    "assessment-service",
    "notification-service",
    "monitoring-service",
    "report-service",
    "adaptive-education-service"
)

foreach ($service in $services) {

    $path = Join-Path $root "$service\src\main\java"

    if (!(Test-Path $path)) {
        Write-Host "[WARN] Skipping $service — no src/main/java" -ForegroundColor Yellow
        continue
    }

    Write-Host "[INFO] Reparando archivos en: $service" -ForegroundColor Cyan

    $javaFiles = Get-ChildItem -Path $path -Filter "*.java" -Recurse

    foreach ($file in $javaFiles) {

        Write-Host "  -> Cleaning $($file.FullName)"

        # Leer contenido RAW para detectar BOM
        $raw = Get-Content $file.FullName -Raw -Encoding Byte

        # Si tiene BOM UTF-8, quitarlo
        if ($raw.Length -ge 3 -and $raw[0] -eq 0xEF -and $raw[1] -eq 0xBB -and $raw[2] -eq 0xBF) {
            Write-Host "     [FIX] BOM encontrado — removido"
            $raw = $raw[3..($raw.Length-1)]
        }

        # Convertir bytes a string sin BOM
        $text = [System.Text.Encoding]::UTF8.GetString($raw)

        # Reparar strings escapados incorrectamente del generador
        $text = $text -replace "\\\"" , '"'
        $text = $text -replace "\\/" , '/'
        $text = $text -replace "\\\)" , ')'
        $text = $text -replace "\\\(" , '('

        # Guardar como UTF-8 sin BOM
        [System.IO.File]::WriteAllLines($file.FullName, $text, (New-Object System.Text.UTF8Encoding($false)))
    }
}

Write-Host "==============================================="
Write-Host "   FIX COMPLETO — fuentes Java reparadas"
Write-Host "   Ahora ejecuta: mvn clean package -am"
Write-Host "==============================================="
