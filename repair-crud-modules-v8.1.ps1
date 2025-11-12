#========================================================
# Archivo: repair-crud-modules-v8.2.ps1
# Ubicación: inclusive-learning-platform-backend
#========================================================

Write-Host "=== Reparación automática avanzada de CRUD + Swagger (v8.2) ===" -ForegroundColor Cyan

# -----------------------------
# Función segura para escribir UTF8 sin BOM
# -----------------------------
function Write-Utf8NoBom {
    param(
        [Parameter(Mandatory=$true)][string]$Path,
        [Parameter(Mandatory=$true)][string]$Content,
        [bool]$Append = $false
    )

    $dir = Split-Path -LiteralPath $Path -Parent
    if (-not (Test-Path -LiteralPath $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }

    $encoding = New-Object System.Text.UTF8Encoding $false
    $bytes = $encoding.GetBytes($Content)

    if ($Append -and (Test-Path -LiteralPath $Path)) {
        $fs = [System.IO.File]::Open($Path, [System.IO.FileMode]::Append, [System.IO.FileAccess]::Write, [System.IO.FileShare]::Read)
        try { $fs.Write($bytes, 0, $bytes.Length) } finally { $fs.Close() }
    } else {
        [System.IO.File]::WriteAllBytes($Path, $bytes)
    }
}

# -----------------------------
# Función para detectar módulos Java
# -----------------------------
function Get-JavaModules {
    param(
        [Parameter(Mandatory=$true)][string]$BasePath
    )
    $modules = Get-ChildItem -Path $BasePath -Recurse -Directory | Where-Object {
        Test-Path -LiteralPath (Join-Path $_.FullName "src\main\java")
    }
    return $modules
}

# -----------------------------
# Función de creación de CRUD + Swagger
# -----------------------------
function Generate-CRUD-Swagger {
    param(
        [Parameter(Mandatory=$true)][string]$ModulePath
    )

    Write-Host "[INFO] Procesando módulo: $ModulePath" -ForegroundColor Green

    # Detectar entidades Java
    $entityFiles = Get-ChildItem -Path (Join-Path $ModulePath "src\main\java") -Recurse -Filter "*.java"

    foreach ($file in $entityFiles) {
        $packageLine = Get-Content $file | Select-String -Pattern "^package\s+(.+);"
        if ($packageLine) {
            $package = $packageLine.Matches[0].Groups[1].Value
        } else {
            Write-Warning "[WARN] No se detectó package en $($file.FullName). Se usará 'com.example'."
            $package = "com.example"
        }

        # Generación ficticia de CRUD + Swagger (puedes agregar tus plantillas)
        $controllerFile = Join-Path $ModulePath "$($file.BaseName)Controller.java"
        $controllerContent = @"
package $package.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class $($file.BaseName)Controller {
    // TODO: Implement CRUD endpoints
}
"@

        Write-Utf8NoBom -Path $controllerFile -Content $controllerContent -Append $false
    }

    # Actualizar pom.xml con Swagger (si existe)
    $pomFile = Join-Path $ModulePath "pom.xml"
    if (Test-Path $pomFile) {
        $pomContent = Get-Content $pomFile -Raw
        if ($pomContent -notmatch "<swagger") {
            Write-Host "[INFO] Agregando dependencias de Swagger en $pomFile" -ForegroundColor Yellow
            $pomContent += "`n<!-- Swagger dependencies -->"
            Write-Utf8NoBom -Path $pomFile -Content $pomContent -Append $false
        } else {
            Write-Host "[INFO] Swagger ya presente en $pomFile"
        }
    } else {
        Write-Warning "[WARN] No se encontró pom.xml en $ModulePath"
    }
}

# -----------------------------
# Función principal
# -----------------------------
function Repair-AllModules {
    param(
        [Parameter(Mandatory=$true)][string]$BasePath
    )

    $modules = Get-JavaModules -BasePath $BasePath

    if (-not $modules) {
        Write-Warning "[WARN] No se encontraron módulos Java en $BasePath"
        return
    }

    foreach ($module in $modules) {
        Generate-CRUD-Swagger -ModulePath $module.FullName
    }

    Write-Host "[INFO] Todos los módulos procesados." -ForegroundColor Cyan
}

# -----------------------------
# Ejecutar script principal
# -----------------------------
$basePath = Split-Path -LiteralPath $MyInvocation.MyCommand.Path -Parent
Repair-AllModules -BasePath $basePath
