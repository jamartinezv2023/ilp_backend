# Requires: PowerShell 5+ (Windows) o 7+ (Core)
# Objetivo: Completar CRUD por entidad (Controller + Service + ServiceImpl in-memory) sin sobrescribir
# Reportes: genera module_crud_fix_<timestamp>.csv y module_crud_fix_<timestamp>.html

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

# ---------- Helpers ----------
function New-Dir {
    param([string]$Path)
    if (-not [string]::IsNullOrWhiteSpace($Path) -and -not (Test-Path -LiteralPath $Path)) {
        New-Item -ItemType Directory -Path $Path | Out-Null
    }
}

function Read-PackageAndClass {
    param([string]$File)
    $pkg   = ''
    $class = ''
    Get-Content -LiteralPath $File -Encoding UTF8 | ForEach-Object {
        if (-not $pkg)   { if ($_ -match '^\s*package\s+([A-Za-z0-9_.]+)\s*;') { $pkg = $matches[1] } }
        if (-not $class) { if ($_ -match '^\s*public\s+class\s+([A-Za-z0-9_]+)\b') { $class = $matches[1] } }
    }
    return @{ Package = $pkg; Class = $class }
}

function To-Kebab {
    param([string]$Name)
    return ([regex]::Replace($Name,'(?<=.)([A-Z])','-$1')).ToLower()
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


# ---------- Plantillas (here-strings de comillas simples para no expandir variables) ----------
$TemplateController = @'
package {controllerPackage};

import {servicePackage}.{EntityName}Service;
import {entityPackage}.{EntityName};
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/{endpointBase}")
public class {EntityName}Controller {

    private final {EntityName}Service service;

    public {EntityName}Controller({EntityName}Service service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<{EntityName}>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<{EntityName}> findById(@PathVariable Long id) {
        {EntityName} e = service.findById(id);
        return e != null ? ResponseEntity.ok(e) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<{EntityName}> create(@RequestBody {EntityName} body) {
        return ResponseEntity.ok(service.create(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<{EntityName}> update(@PathVariable Long id, @RequestBody {EntityName} body) {
        {EntityName} e = service.update(id, body);
        return e != null ? ResponseEntity.ok(e) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean ok = service.delete(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
'@

$TemplateService = @'
package {servicePackage};

import {entityPackage}.{EntityName};
import java.util.List;

public interface {EntityName}Service {
    List<{EntityName}> findAll();
    {EntityName} findById(Long id);
    {EntityName} create({EntityName} e);
    {EntityName} update(Long id, {EntityName} e);
    boolean delete(Long id);
}
'@

$TemplateServiceImpl = @'
package {implPackage};

import {servicePackage}.{EntityName}Service;
import {entityPackage}.{EntityName};
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class {EntityName}ServiceImpl implements {EntityName}Service {

    private final Map<Long, {EntityName}> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public List<{EntityName}> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public {EntityName} findById(Long id) {
        return store.get(id);
    }

    @Override
    public {EntityName} create({EntityName} e) {
        long id = idGen.getAndIncrement();
        try {
            // Si la entidad tiene setId(Long), úsalo; si no, ignora
            e.getClass().getMethod("setId", Long.class).invoke(e, id);
        } catch (Exception ignored) {}
        store.put(id, e);
        return e;
    }

    @Override
    public {EntityName} update(Long id, {EntityName} e) {
        if (!store.containsKey(id)) return null;
        try {
            e.getClass().getMethod("setId", Long.class).invoke(e, id);
        } catch (Exception ignored) {}
        store.put(id, e);
        return e;
    }

    @Override
    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}
'@

# ---------- Descubrir módulos ----------
$repoRoot = (Get-Location).Path
$modules = @()

# Heurística 1: cualquier carpeta *-service
$modules += Get-ChildItem -LiteralPath $repoRoot -Directory | Where-Object { $_.Name -like '*-service' }

# Heurística 2: módulos raices que tengan src/main/java (por si hay uno agrupador)
$modules += Get-ChildItem -LiteralPath $repoRoot -Directory | Where-Object {
    Test-Path (Join-Path $_.FullName 'src/main/java')
}

# Normalizar y quitar duplicados
$modules = $modules | Sort-Object FullName -Unique

if (-not $modules) {
    Write-Host "[WARN] No se detectaron módulos con src/main/java" -ForegroundColor Yellow
    exit 0
}

# ---------- Recorrer entidades por módulo ----------
$report = @()
$ts = (Get-Date).ToString('yyyy-MM-dd_HH-mm-ss')
$csvPath  = Join-Path $repoRoot ("module_crud_fix_{0}.csv" -f $ts)
$htmlPath = Join-Path $repoRoot ("module_crud_fix_{0}.html" -f $ts)

foreach ($m in $modules) {
    $moduleName = Split-Path $m.FullName -Leaf
    $javaRoot   = Join-Path $m.FullName 'src/main/java'
    if (-not (Test-Path $javaRoot)) { continue }

    # Entities bajo **/entity/*.java
    $entityFiles = Get-ChildItem -LiteralPath $javaRoot -Recurse -Filter '*.java' -ErrorAction SilentlyContinue `
                   | Where-Object { $_.FullName -match '\\entity\\' }

    if (-not $entityFiles) {
        # Sin entities: registrar y seguir
        $report += [pscustomobject]@{
            Module          = $moduleName
            Entity          = '(none)'
            Controller      = 'n/a'
            Service         = 'n/a'
            ServiceImpl     = 'n/a'
            Status          = 'No entities'
            Paths           = ''
        }
        continue
    }

    foreach ($ef in $entityFiles) {
        $info = Read-PackageAndClass -File $ef.FullName
        $entityPackage = $info.Package
        $entityName    = $info.Class

        if ([string]::IsNullOrWhiteSpace($entityPackage) -or [string]::IsNullOrWhiteSpace($entityName)) {
            $report += [pscustomobject]@{
                Module          = $moduleName
                Entity          = "(unparsed: $($ef.Name))"
                Controller      = 'error'
                Service         = 'error'
                ServiceImpl     = 'error'
                Status          = 'Entity parse failed'
                Paths           = $ef.FullName
            }
            continue
        }

        # Base package deducido (quitando .entity.*)
        $packageBase = ($entityPackage -replace '\.entity(\..*)?$','')
        $controllerPackage = "$packageBase.controller"
        $servicePackage    = "$packageBase.service"
        $implPackage       = "$servicePackage.impl"

        # Rutas destino
        $controllerDir = Join-Path $javaRoot ($controllerPackage -replace '\.','\')
        $serviceDir    = Join-Path $javaRoot ($servicePackage    -replace '\.','\')
        $implDir       = Join-Path $javaRoot ($implPackage       -replace '\.','\')

        $controllerPath = Join-Path $controllerDir ("{0}Controller.java" -f $entityName)
        $servicePath    = Join-Path $serviceDir    ("{0}Service.java"    -f $entityName)
        $implPath       = Join-Path $implDir       ("{0}ServiceImpl.java"-f $entityName)

        $controllerExists = Test-Path -LiteralPath $controllerPath
        $serviceExists    = Test-Path -LiteralPath $servicePath
        $implExists       = Test-Path -LiteralPath $implPath

        # Crear Service si falta
        if (-not $serviceExists) {
            New-Dir $serviceDir
            $svc = $TemplateService.Replace('{servicePackage}',$servicePackage).Replace('{entityPackage}',$entityPackage).Replace('{EntityName}',$entityName)
            Write-TextFileUtf8NoBom -Path $servicePath -Content $svc
            $serviceExists = $true
        }

        # Crear ServiceImpl si falta
        if (-not $implExists) {
            New-Dir $implDir
            $sim = $TemplateServiceImpl.Replace('{implPackage}',$implPackage).Replace('{servicePackage}',$servicePackage).Replace('{entityPackage}',$entityPackage).Replace('{EntityName}',$entityName)
            Write-TextFileUtf8NoBom -Path $implPath -Content $sim
            $implExists = $true
        }

        # Crear Controller si falta
        if (-not $controllerExists) {
            New-Dir $controllerDir
            $endpointBase = (To-Kebab $entityName) + "s"
            $ctr = $TemplateController.
                Replace('{controllerPackage}',$controllerPackage).
                Replace('{servicePackage}',$servicePackage).
                Replace('{entityPackage}',$entityPackage).
                Replace('{EntityName}',$entityName).
                Replace('{endpointBase}',$endpointBase)
            Write-TextFileUtf8NoBom -Path $controllerPath -Content $ctr
            $controllerExists = $true
        }

        $status = if ($controllerExists -and $serviceExists -and $implExists) { 'Complete' } else { 'Partial' }

        $report += [pscustomobject]@{
            Module          = $moduleName
            Entity          = $entityName
            Controller      = $(if($controllerExists){'OK'}else{'Missing'})
            Service         = $(if($serviceExists){'OK'}else{'Missing'})
            ServiceImpl     = $(if($implExists){'OK'}else{'Missing'})
            Status          = $status
            Paths           = "entity=$($ef.FullName); controller=$controllerPath; service=$servicePath; impl=$implPath"
        }
    }
}

# ---------- Guardar CSV ----------
$report | Export-Csv -LiteralPath $csvPath -NoTypeInformation -Encoding UTF8

# ---------- Guardar HTML ----------
$rows = $report | ForEach-Object {
@"
<tr>
  <td>$($_.Module)</td>
  <td>$($_.Entity)</td>
  <td>$($_.Controller)</td>
  <td>$($_.Service)</td>
  <td>$($_.ServiceImpl)</td>
  <td>$($_.Status)</td>
</tr>
"@
} | Out-String

$html = @"
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Inclusive Learning Platform - Module CRUD Fix Report</title>
<style>
body{font-family:Segoe UI,Arial,Helvetica,sans-serif;margin:20px}
h1{font-size:20px;margin-bottom:8px}
table{border-collapse:collapse;width:100%}
th,td{border:1px solid #ddd;padding:8px}
th{background:#f4f4f4;text-align:left}
tr:nth-child(even){background:#fafafa}
.badge{padding:2px 6px;border-radius:4px}
.ok{background:#e6ffed;border:1px solid #b7eb8f}
.miss{background:#fff1f0;border:1px solid #ffa39e}
</style>
</head>
<body>
<h1>Inclusive Learning Platform - Module CRUD Fix Report</h1>
<p>Generated at: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")</p>
<table>
<thead>
<tr><th>Module</th><th>Entity</th><th>Controller</th><th>Service</th><th>ServiceImpl</th><th>Status</th></tr>
</thead>
<tbody>
$rows
</tbody>
</table>
<p>CSV: $([System.Web.HttpUtility]::HtmlEncode($csvPath))</p>
</body>
</html>
"@

Write-TextFileUtf8NoBom -Path $htmlPath -Content $html

Write-Host "=== CRUD completion finished ===" -ForegroundColor Green
Write-Host "CSV report : $csvPath"
Write-Host "HTML report: $htmlPath"
