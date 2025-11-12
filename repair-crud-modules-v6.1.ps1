<#
repair-crud-modules-v6.1.ps1
Repara automáticamente controladores CRUD faltantes y genera servicios in-memory.
Compatible con Windows PowerShell 5.x y PowerShell 7.
#>

Write-Host "=== Reparación automática de CRUD (v6.1) ===" -ForegroundColor Cyan

# ==== Funciones utilitarias ====

function New-Dir {
    param([string]$Path)
    if (-not (Test-Path $Path)) {
        New-Item -ItemType Directory -Path $Path | Out-Null
    }
}

function Write-TextFileUtf8NoBom {
    param([string]$Path,[string]$Content)
    $parentDir = Split-Path $Path -Parent
    if (-not [string]::IsNullOrWhiteSpace($parentDir)) {
        New-Dir $parentDir
    }
    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($Path, $Content, $utf8NoBom)
}

function HtmlEncode {
    param([string]$text)
    if ($null -eq $text) { return "" }
    return $text -replace '&','&amp;' -replace '<','&lt;' -replace '>','&gt;' -replace '"','&quot;'
}

# ==== Paths y estructuras ====

$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$csvPath   = "module_crud_fix_$timestamp.csv"
$htmlPath  = "module_crud_fix_$timestamp.html"

$basePath = Get-Location

$modules = Get-ChildItem -Directory | Where-Object { $_.Name -like "*-service" }

$report = @()

# ==== Procesamiento de módulos ====

foreach ($mod in $modules) {
    $moduleName = $mod.Name
    Write-Host "`n[INFO] Procesando módulo: $moduleName" -ForegroundColor Yellow

    $entityPath = Join-Path $mod.FullName "src\main\java"
    $entities = Get-ChildItem -Path $entityPath -Recurse -Filter *.java -ErrorAction SilentlyContinue | Where-Object { $_.FullName -match "\\entity\\" }

    if (-not $entities) {
        $report += [pscustomobject]@{
            Module=$moduleName; Entity="(sin entidades)"; Controller="No"; Service="No"; ServiceImpl="No"; Status="Sin entidad"
        }
        continue
    }

    foreach ($entity in $entities) {
        $entityName = [System.IO.Path]::GetFileNameWithoutExtension($entity.Name)
        $entityLower = $entityName.Substring(0,1).ToLower() + $entityName.Substring(1)
        $packageBase = ($entity.Directory.FullName -split "src\\main\\java\\")[1] -replace "\\entity.*",""
        $packageBase = $packageBase -replace "\\","."

        # === Controlador ===
        $controllerDir = Join-Path $entity.Directory.Parent.FullName "controller"
        $controllerPath = Join-Path $controllerDir "$entityName`Controller.java"

        if (-not (Test-Path $controllerPath)) {
            $controllerContent = @"
package $packageBase.controller;

import $packageBase.entity.$entityName;
import $packageBase.service.$entityName`Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/${entityLower}s")
public class ${entityName}Controller {
    private final ${entityName}Service service;

    public ${entityName}Controller(${entityName}Service service) {
        this.service = service;
    }

    @GetMapping
    public List<$entityName> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public $entityName getById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public $entityName create(@RequestBody $entityName data) { return service.save(data); }

    @PutMapping("/{id}")
    public $entityName update(@PathVariable Long id, @RequestBody $entityName data) { return service.update(id, data); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
"@
            Write-TextFileUtf8NoBom $controllerPath $controllerContent
            $ctrlStatus = "Creado"
        } else {
            $ctrlStatus = "Existente"
        }

        # === Service ===
        $serviceDir = Join-Path $entity.Directory.Parent.FullName "service"
        $servicePath = Join-Path $serviceDir "$entityName`Service.java"

        if (-not (Test-Path $servicePath)) {
            $serviceContent = @"
package $packageBase.service;

import java.util.List;
import $packageBase.entity.$entityName;

public interface ${entityName}Service {
    List<$entityName> findAll();
    $entityName findById(Long id);
    $entityName save($entityName data);
    $entityName update(Long id, $entityName data);
    void delete(Long id);
}
"@
            Write-TextFileUtf8NoBom $servicePath $serviceContent
            $srvStatus = "Creado"
        } else {
            $srvStatus = "Existente"
        }

        # === ServiceImpl ===
        $implDir = Join-Path $serviceDir "impl"
        $implPath = Join-Path $implDir "$entityName`ServiceImpl.java"

        if (-not (Test-Path $implPath)) {
            $implContent = @"
package $packageBase.service.impl;

import $packageBase.entity.$entityName;
import $packageBase.service.${entityName}Service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ${entityName}ServiceImpl implements ${entityName}Service {
    private final Map<Long, $entityName> repo = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public List<$entityName> findAll() { return new ArrayList<>(repo.values()); }

    @Override
    public $entityName findById(Long id) { return repo.get(id); }

    @Override
    public $entityName save($entityName data) {
        try {
            java.lang.reflect.Method setId = data.getClass().getMethod("setId", Long.class);
            setId.invoke(data, idCounter);
        } catch (Exception ignored) {}
        repo.put(idCounter, data);
        idCounter++;
        return data;
    }

    @Override
    public $entityName update(Long id, $entityName data) {
        if (repo.containsKey(id)) {
            repo.put(id, data);
            return data;
        }
        return null;
    }

    @Override
    public void delete(Long id) { repo.remove(id); }
}
"@
            Write-TextFileUtf8NoBom $implPath $implContent
            $implStatus = "Creado"
        } else {
            $implStatus = "Existente"
        }

        $status = if ($ctrlStatus -eq "Creado" -or $srvStatus -eq "Creado" -or $implStatus -eq "Creado") { "Actualizado" } else { "Completo" }

        $report += [pscustomobject]@{
            Module=$moduleName; Entity=$entityName;
            Controller=$ctrlStatus; Service=$srvStatus; ServiceImpl=$implStatus;
            Status=$status
        }
    }
}

# ==== Guardar CSV ====

$report | Export-Csv -Path $csvPath -NoTypeInformation -Encoding UTF8
Write-Host "`n[OK] Reporte CSV generado en: $csvPath" -ForegroundColor Green

# ==== Generar HTML ====

$rows = $report | ForEach-Object {
    "<tr><td>$($_.Module)</td><td>$($_.Entity)</td><td>$($_.Controller)</td><td>$($_.Service)</td><td>$($_.ServiceImpl)</td><td>$($_.Status)</td></tr>"
} | Out-String

$html = @"
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Reporte CRUD $timestamp</title>
<style>
body { font-family: Arial; margin: 20px; }
table { border-collapse: collapse; width: 100%; }
th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
th { background-color: #444; color: white; }
tr:nth-child(even) { background-color: #f2f2f2; }
.ok { color: green; } .warn { color: orange; } .err { color: red; }
</style>
</head>
<body>
<h2>Reporte de Módulos CRUD - $timestamp</h2>
<p>CSV: $(HtmlEncode $csvPath)</p>
<table>
<tr><th>Módulo</th><th>Entidad</th><th>Controlador</th><th>Servicio</th><th>Impl</th><th>Estado</th></tr>
$rows
</table>
</body>
</html>
"@

Write-TextFileUtf8NoBom $htmlPath $html
Write-Host "[OK] Reporte HTML generado en: $htmlPath" -ForegroundColor Green

Write-Host "`n=== Reparación completada correctamente (v6.1) ===" -ForegroundColor Cyan
