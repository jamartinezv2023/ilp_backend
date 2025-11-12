# ---------------------------------------------------------------
# Inclusive Learning Platform - Verificación y Reporte de Módulos
# Autor: José Martínez
# Versión: Avanzada con validación CRUD + Reporte CSV y HTML
# ---------------------------------------------------------------

Write-Host ""
Write-Host "=== Verificando estructura y CRUD de microservicios ==="
Write-Host ""

# Fecha actual para reporte
$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm"
$csvReport = "module_crud_report_$timestamp.csv"
$htmlReport = "module_crud_report_$timestamp.html"

# Encabezado CSV
"Module,HasController,HasCRUD,Status" | Out-File -FilePath $csvReport -Encoding utf8

# Buscar todos los módulos con pom.xml
$modules = Get-ChildItem -Recurse -Include "pom.xml" -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -notmatch "target" }

# Crear función de validación CRUD
function Test-ControllerCRUD {
    param ([string]$filePath)

    $content = Get-Content $filePath -Raw
    $hasGetAll = $content -match "GetMapping\s*\("
    $hasGetById = $content -match "GetMapping\s*\(.*id"
    $hasPost = $content -match "PostMapping"
    $hasPut = $content -match "PutMapping"
    $hasDelete = $content -match "DeleteMapping"

    $total = ($hasGetAll + $hasGetById + $hasPost + $hasPut + $hasDelete)
    if ($total -eq 5) {
        return "Complete"
    } elseif ($total -ge 3) {
        return "Partial"
    } else {
        return "Missing"
    }
}

foreach ($pom in $modules) {
    $modulePath = Split-Path $pom.FullName -Parent
    $moduleName = Split-Path $modulePath -Leaf
    Write-Host "Revisando módulo: $moduleName"

    $src = Join-Path $modulePath "src/main/java"
    $hasController = $false
    $crudStatus = "Missing"

    if (Test-Path $src) {

        # Asegurar carpetas
        foreach ($folder in @("controller", "service", "repository", "entity")) {
            $folderPath = Join-Path $src $folder
            if (-not (Test-Path $folderPath)) {
                New-Item -ItemType Directory -Path $folderPath | Out-Null
                Write-Host "   [+] Creada carpeta faltante: $folder"
            }
        }

        # Buscar controladores
        $controllerFiles = Get-ChildItem -Recurse -Path $src -Include "*Controller.java" -ErrorAction SilentlyContinue
        if ($controllerFiles.Count -gt 0) {
            $hasController = $true
            Write-Host "   [=] Controlador detectado."
            $crudStatus = "Missing"

            foreach ($ctrl in $controllerFiles) {
                $crudStatus = Test-ControllerCRUD -filePath $ctrl.FullName
                if ($crudStatus -eq "Complete") { break }
            }

            if ($crudStatus -eq "Complete") {
                Write-Host "   [+] CRUD completo."
            } elseif ($crudStatus -eq "Partial") {
                Write-Host "   [~] CRUD parcial."
            } else {
                Write-Host "   [!] CRUD ausente o incompleto."
            }

        } else {
            # Crear CRUD básico si no hay controlador
            $crudController = @"
package com.inclusive.${moduleName}.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/${moduleName}")
public class ${moduleName}Controller {

    @GetMapping
    public List<String> findAll() {
        return List.of("Sample data for ${moduleName}");
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id) {
        return "Sample ${moduleName} #" + id;
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "Created ${moduleName}: " + body;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String body) {
        return "Updated ${moduleName} #" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted ${moduleName} #" + id;
    }
}
"@
            $controllerDir = Join-Path $src "controller"
            $crudFile = Join-Path $controllerDir "${moduleName}Controller.java"
            $crudController | Out-File -FilePath $crudFile -Encoding utf8
            Write-Host "   [+] CRUD básico creado para $moduleName"
            $hasController = $true
            $crudStatus = "Complete"
        }
    }
    else {
        Write-Host "   [!] Estructura src/main/java no encontrada."
    }

    # Determinar estado general
    if (-not $hasController) {
        $status = "No Controller"
    } else {
        $status = $crudStatus
    }

    # Agregar al CSV
    "$moduleName,$hasController,$crudStatus,$status" | Add-Content -Path $csvReport
}

Write-Host ""
Write-Host "=== Generando reporte ==="

# Convertir CSV a HTML simple
$htmlHeader = @"
<html><head><meta charset='UTF-8'><title>Module CRUD Report</title>
<style>
body { font-family: Arial; background: #fafafa; margin: 20px; }
table { border-collapse: collapse; width: 100%; }
th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
th { background-color: #333; color: white; }
tr:nth-child(even) { background: #f2f2f2; }
</style></head><body>
<h2>Inclusive Learning Platform - Module CRUD Report</h2>
<p>Fecha: $timestamp</p>
<table><tr><th>Module</th><th>Has Controller</th><th>Has CRUD</th><th>Status</th></tr>
"@

$htmlFooter = "</table></body></html>"

$htmlBody = Get-Content $csvReport | Select-Object -Skip 1 | ForEach-Object {
    $cols = $_ -split ","
    "<tr><td>$($cols[0])</td><td>$($cols[1])</td><td>$($cols[2])</td><td>$($cols[3])</td></tr>"
}

$htmlHeader + ($htmlBody -join "`n") + $htmlFooter | Out-File $htmlReport -Encoding utf8

Write-Host ""
Write-Host "Reporte generado:"
Write-Host "  - CSV:  $csvReport"
Write-Host "  - HTML: $htmlReport"
Write-Host ""
Write-Host "=== Verificación completa ==="
Write-Host ""
