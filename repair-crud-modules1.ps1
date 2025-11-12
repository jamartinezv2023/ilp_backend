# ---------------------------------------------------------------
# Inclusive Learning Platform - Reparador Automático de CRUD (v3)
# Corrige rutas anidadas duplicadas como ...\src\main\java\controller\src\main\java
# ---------------------------------------------------------------

Write-Host ""
Write-Host "=== Iniciando reparación automática de CRUD incompletos ==="
Write-Host ""

# Buscar reporte previo
$report = Get-ChildItem -Filter "module_crud_report_*.csv" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $report) {
    Write-Host "⚠️  No se encontró un reporte CSV previo. Ejecuta primero validate-and-report-modules.ps1"
    exit
}

Write-Host "Usando reporte: $($report.Name)"
$modules = Import-Csv $report.FullName
$logFile = "repair_log_$((Get-Date).ToString('yyyy-MM-dd_HH-mm')).txt"

# Función auxiliar para crear rutas seguras
function Ensure-Path {
    param([string]$path)
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
    }
}

# Limpieza de rutas erróneas
function Normalize-Path {
    param([string]$path)
    $path -replace "(\\src\\main\\java)+", "\\src\\main\\java"
}

foreach ($module in $modules) {
    if (($module.Status -eq "Missing") -or ($module.Status -eq "Partial")) {
        Write-Host ""
        Write-Host "➡️  Reparando módulo: $($module.Module)"

        $modulePom = Get-ChildItem -Recurse -Include "pom.xml" -ErrorAction SilentlyContinue |
            Where-Object { $_.FullName -like "*$($module.Module)*" -and $_.FullName -notmatch "target" } |
            Select-Object -First 1

        if (-not $modulePom) {
            Write-Host "   ⚠️  No se encontró pom.xml para $($module.Module)"
            continue
        }

        $modulePath = Split-Path $modulePom.FullName -Parent
        $src = Join-Path $modulePath "src\main\java"

        if (-not (Test-Path $src)) {
            Write-Host "   ⚠️  No se encontró carpeta src/main/java en $modulePath"
            continue
        }

        # Buscar el paquete base real (donde está el código Java principal)
        $packageRoot = Get-ChildItem -Recurse -Path $src -Directory |
            Where-Object { $_.FullName -match "com\\inclusive" } |
            Select-Object -First 1

        if ($packageRoot) {
            $basePath = $packageRoot.FullName
        } else {
            $basePath = $src
        }

        # Limpiar duplicados de "src/main/java"
        $basePath = Normalize-Path $basePath

        # Crear carpeta controller dentro del paquete base
        $controllerDir = Join-Path $basePath "controller"
        Ensure-Path $controllerDir

        # Buscar si ya existe controlador
        $controllerFile = Get-ChildItem -Recurse -Path $controllerDir -Include "*Controller.java" -ErrorAction SilentlyContinue | Select-Object -First 1
        if (-not $controllerFile) {
            Write-Host "   ✏️  No existe controlador, creando uno nuevo..."
            $controllerPath = Join-Path $controllerDir "$($module.Module)Controller.java"

            $crudTemplate = @"
package com.inclusive.${module.Module}.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/${module.Module}")
public class ${module.Module}Controller {

    // Auto-generated CRUD methods

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for ${module.Module}");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample ${module.Module} #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created ${module.Module}: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated ${module.Module} #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted ${module.Module} #" + id;
    }
}
"@
            # Crear archivo controlador
            $crudTemplate | Out-File -FilePath $controllerPath -Encoding utf8
            Add-Content -Path $logFile -Value "   [+] Controlador creado: $controllerPath"
            Write-Host "   [+] Controlador creado correctamente: $controllerPath"
        } else {
            Write-Host "   [=] Controlador ya existe: $($controllerFile.Name)"
        }
    }
}

Write-Host ""
Write-Host "=== Reparación completada ==="
Write-Host "Consulta el registro: $logFile"
Write-Host ""
