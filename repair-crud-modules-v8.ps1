# ===============================================================
# File: repair-crud-modules-v8.ps1
# Descripción: Reparación automática de CRUD + Swagger (versión 8)
# Autor: José Alfredo Martínez Valdés
# Versión: v8.1 (corregida para compatibilidad con PowerShell)
# ===============================================================

param(
    [string]$ModuleName = "inclusive-learning-platform-backend"
)

Write-Host "=== Reparación automática de CRUD + Swagger (v8) ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "[INFO] Módulo: $ModuleName" -ForegroundColor Yellow

# ---------------------------------------------------------------------
# FUNCIONES AUXILIARES
# ---------------------------------------------------------------------

function Write-Utf8NoBomSafe {
    param (
        [Parameter(Mandatory = $true)]
        [string] $FilePath,

        [Parameter(Mandatory = $true)]
        [string] $Content
    )

    try {
        # Usa .NET para escribir el archivo en UTF-8 sin BOM
        $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
        [System.IO.File]::WriteAllText($FilePath, $Content, $utf8NoBom)
        Write-Host "[OK] Guardado (UTF-8 sin BOM): $FilePath" -ForegroundColor Green
    }
    catch {
        Write-Host "[ERROR] No se pudo guardar el archivo: $FilePath" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor DarkRed
    }
}

# ---------------------------------------------------------------------
# FUNCIONES PRINCIPALES
# ---------------------------------------------------------------------

function Repair-PomFile {
    param(
        [string]$PomPath
    )

    if (Test-Path $PomPath) {
        Write-Host "[INFO] Reparando POM en: $PomPath" -ForegroundColor Cyan
        $pomText = Get-Content $PomPath -Raw

        # Ajustes de ejemplo: asegúrate de incluir dependencias necesarias
        if ($pomText -notmatch "springdoc-openapi-starter-webmvc-ui") {
            $pomText = $pomText -replace "(?s)(</dependencies>)", @"
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>
`$1
"@
            Write-Host "[OK] Dependencia Springdoc añadida" -ForegroundColor Green
        }
        else {
            Write-Host "[OK] Dependencia Springdoc ya presente" -ForegroundColor DarkGreen
        }

        Write-Utf8NoBomSafe -FilePath $PomPath -Content $pomText
    }
    else {
        Write-Host "[ERROR] No se encontró el archivo POM: $PomPath" -ForegroundColor Red
    }
}

function Repair-ApplicationFile {
    param(
        [string]$AppPath
    )

    if (Test-Path $AppPath) {
        Write-Host "[INFO] Verificando archivo principal de aplicación: $AppPath" -ForegroundColor Cyan
        $content = Get-Content $AppPath -Raw

        if ($content -notmatch "@OpenAPIDefinition") {
            $swaggerConfig = @"
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Inclusive Learning API", version = "1.0", description = "Documentación automática generada con Swagger / OpenAPI"))
"@
            $content = $content -replace "(package\s+[^\n]+)", "`$1`r`n$swaggerConfig"
            Write-Host "[OK] Anotación @OpenAPIDefinition añadida" -ForegroundColor Green
        }

        Write-Utf8NoBomSafe -FilePath $AppPath -Content $content
    }
    else {
        Write-Host "[ERROR] No se encontró el archivo principal de aplicación: $AppPath" -ForegroundColor Red
    }
}

function Repair-CrudModules {
    param(
        [string]$ModuleRoot
    )

    Write-Host "[INFO] Buscando módulos CRUD..." -ForegroundColor Yellow
    $controllerPaths = Get-ChildItem -Path $ModuleRoot -Recurse -Include "*Controller.java" -ErrorAction SilentlyContinue

    foreach ($controller in $controllerPaths) {
        Write-Host "[INFO] Verificando $($controller.Name)" -ForegroundColor Cyan
        $code = Get-Content $controller.FullName -Raw

        if ($code -notmatch "@RestController") {
            $code = $code -replace "(?m)^(package\s+[^\n]+)", "`$1`r`nimport org.springframework.web.bind.annotation.RestController;`r`n@RestController"
            Write-Host "[OK] Anotación @RestController añadida" -ForegroundColor Green
        }

        if ($code -notmatch "@RequestMapping") {
            $basePath = "/" + ($controller.BaseName -replace "Controller", "").ToLower()
            $code = $code -replace "(@RestController)", "`$1`r`n@RequestMapping(`"$basePath`")"
            Write-Host "[OK] Añadido @RequestMapping('$basePath')" -ForegroundColor Green
        }

        Write-Utf8NoBomSafe -FilePath $controller.FullName -Content $code
    }

    if (-not $controllerPaths) {
        Write-Host "[ADVERTENCIA] No se encontraron controladores CRUD en $ModuleRoot" -ForegroundColor DarkYellow
    }
}

# ---------------------------------------------------------------------
# PROCESO PRINCIPAL
# ---------------------------------------------------------------------

$PomPath = Join-Path $PWD "pom.xml"
$AppFile = Get-ChildItem -Path $PWD -Recurse -Include "*Application.java" -ErrorAction SilentlyContinue | Select-Object -First 1

Repair-PomFile -PomPath $PomPath

if ($AppFile) {
    Repair-ApplicationFile -AppPath $AppFile.FullName
} else {
    Write-Host "[ERROR] No se encontró el archivo principal de la aplicación (*Application.java)" -ForegroundColor Red
}

Repair-CrudModules -ModuleRoot $PWD

Write-Host ""
Write-Host "=== Reparación completada con éxito ===" -ForegroundColor Cyan
