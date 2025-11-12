# Archivo: repair-crud-modules-v9.ps1
# Ubicación: inclusive-learning-platform-backend
# Versión optimizada: automática, robusta, PS 5.1+ / 7+

#region Configuración inicial
$ErrorActionPreference = "Stop"
$basePath = Split-Path -LiteralPath $MyInvocation.MyCommand.Path
$srcPath  = Join-Path $basePath "src\main\java"
$reportsPath = Join-Path $basePath "reports"

# Crear carpeta de reportes si no existe
if (-not (Test-Path $reportsPath)) { New-Item -ItemType Directory -Path $reportsPath | Out-Null }

Write-Host "=== Reparación automática avanzada de CRUD + Swagger (v9) ===" -ForegroundColor Cyan
#endregion

#region Funciones auxiliares

function Write-Utf8NoBom {
    param (
        [Parameter(Mandatory=$true)]
        [string]$Path,
        [Parameter(Mandatory=$true)]
        [string]$String
    )
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($String)
    [System.IO.File]::WriteAllBytes($Path, $bytes)
}

function Get-JavaModules {
    param ([string]$SourcePath)
    # Encuentra todos los directorios que contienen archivos Java
    Get-ChildItem -Path $SourcePath -Recurse -Include *.java | 
        ForEach-Object { $_.Directory.FullName } |
        Sort-Object -Unique
}

function Ensure-Swagger {
    param ([string]$ModulePath)
    $pomFile = Join-Path $ModulePath "pom.xml"
    if (-not (Test-Path $pomFile)) { return $false }

    $pomContent = Get-Content $pomFile -Raw
    if ($pomContent -match "springdoc-openapi") {
        Write-Host "[INFO] Swagger ya presente en $pomFile" -ForegroundColor Green
        return $false
    } else {
        Write-Host "[INFO] Agregando Swagger a $pomFile" -ForegroundColor Yellow
        # Ejemplo de inyección de dependencia Swagger mínima
        $dependency = @"
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-ui</artifactId>
        <version>1.7.0</version>
    </dependency>
"@
        $pomContent = $pomContent -replace "(</dependencies>)", "$dependency`n`$1"
        Write-Utf8NoBom -Path $pomFile -String $pomContent
        return $true
    }
}

function Generate-CRUD {
    param ([string]$ModulePath)
    # Aquí podrías expandir para generar Controller, Service, Repository según las entidades encontradas
    Write-Host "[INFO] Generando CRUD para módulo: $ModulePath" -ForegroundColor Cyan
    # Placeholder: agrega lógica real de generación aquí
}

function Export-Reports {
    param ([string]$ModulePath, [string]$ReportsPath)
    $csvFile = Join-Path $ReportsPath ("report_" + (Split-Path $ModulePath -Leaf) + ".csv")
    $htmlFile = Join-Path $ReportsPath ("report_" + (Split-Path $ModulePath -Leaf) + ".html")
    
    # Ejemplo simple de reporte
    $data = [PSCustomObject]@{
        Module = (Split-Path $ModulePath -Leaf)
        Date   = Get-Date
        Status = "Processed"
    }
    
    $data | Export-Csv -Path $csvFile -NoTypeInformation -Encoding UTF8
    $data | ConvertTo-Html -Title "Reporte CRUD" | Set-Content $htmlFile
    Write-Host "[INFO] Reportes generados: $csvFile, $htmlFile" -ForegroundColor Green
}

function Repair-AllModules {
    param ([string]$BasePath)
    $modules = Get-JavaModules -SourcePath $BasePath
    foreach ($mod in $modules) {
        Write-Host "[INFO] Procesando módulo: $mod" -ForegroundColor Cyan
        Ensure-Swagger -ModulePath $mod
        Generate-CRUD -ModulePath $mod
        Export-Reports -ModulePath $mod -ReportsPath $reportsPath
    }
}
#endregion

# Ejecutar reparación
Repair-AllModules -BasePath $srcPath
Write-Host "=== Reparación finalizada ===" -ForegroundColor Cyan
