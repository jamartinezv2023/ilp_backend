# ---------------------------------------------------------------
# Script seguro: Verifica y completa módulos sin sobrescribir
# Inclusive Learning Platform - Validación de backend modular
# ---------------------------------------------------------------

Write-Host ""
Write-Host "=== Analizando microservicios del backend ==="
Write-Host ""

# Buscar módulos que tengan pom.xml (microservicios Java)
$modules = Get-ChildItem -Recurse -Include "pom.xml" -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -notmatch "target" }

foreach ($pom in $modules) {
    $modulePath = Split-Path $pom.FullName -Parent
    $moduleName = Split-Path $modulePath -Leaf
    Write-Host "Revisando módulo: $moduleName"

    $src = Join-Path $modulePath "src/main/java"
    if (Test-Path $src) {

        # Crear carpetas base si no existen
        foreach ($folder in @("controller", "service", "repository", "entity")) {
            $folderPath = Join-Path $src $folder
            if (-not (Test-Path $folderPath)) {
                New-Item -ItemType Directory -Path $folderPath | Out-Null
                Write-Host "   [+] Creada carpeta faltante: $folder"
            }
        }

        # Buscar controladores existentes
        $controllerFiles = Get-ChildItem -Recurse -Path $src -Include "*Controller.java" -ErrorAction SilentlyContinue
        if ($controllerFiles.Count -eq 0) {
            # Crear CRUD básico
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
        }
        else {
            Write-Host "   [=] Controladores ya existen, no se crean nuevos archivos."
        }
    }
    else {
        Write-Host "   [!] No se encontró estructura src/main/java"
    }
}

Write-Host ""
Write-Host "=== Verificación y completado finalizados ==="
Write-Host ""
