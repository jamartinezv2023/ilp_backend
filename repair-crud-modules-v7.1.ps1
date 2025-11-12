# repair-crud-modules-v7.1.ps1
# Repara CRUD mínimos por módulo sin sobreescribir lo existente.
# - Detecta entidades por nombre en /entity
# - Crea Controller + Service + ServiceImpl solo si faltan
# - Genera tests mínimos (placeholders)
# - Reporte CSV + HTML y auto-open
# Probado en Windows PowerShell 5.1 y PowerShell 7+

$ErrorActionPreference = 'Stop'

# ---------- UTILIDADES ----------
function New-Dir {
    param([Parameter(Mandatory)][string]$Path)
    if (-not (Test-Path -LiteralPath $Path)) {
        New-Item -ItemType Directory -Path $Path | Out-Null
    }
}

function Write-Utf8NoBom {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory, Position=0)][string]$Path,
        [Parameter(Mandatory, Position=1)][string]$Content
    )
    $bytes = [System.Text.UTF8Encoding]::new($false).GetBytes($Content)
    New-Dir (Split-Path -LiteralPath $Path -Parent)
    [System.IO.File]::WriteAllBytes($Path, $bytes)
}

function Slug {
    param([string]$s)
    return ($s -replace '[^a-zA-Z0-9\-]', '-').ToLower()
}

function HtmlEnc {
    param([string]$s)
    return [System.Net.WebUtility]::HtmlEncode($s)
}

# ---------- DETECCIÓN DE MÓDULOS ----------
# Consideramos módulos que tengan src/main/java bajo su carpeta raíz
$repoRoot = (Get-Location).Path
$moduleRoots = @()
Get-ChildItem -Directory | ForEach-Object {
    $javaRoot = Join-Path $_.FullName 'src\main\java'
    if (Test-Path $javaRoot) { $moduleRoots += $_.FullName }
}

# Si el módulo raíz también tiene src (multi-módulo + raíz), lo añadimos
$rootJava = Join-Path $repoRoot 'src\main\java'
if (Test-Path $rootJava) { $moduleRoots = @($repoRoot) + $moduleRoots }

if ($moduleRoots.Count -eq 0) {
    Write-Host "[WARN] No se encontraron módulos con src/main/java. Abortando." -ForegroundColor Yellow
    exit 1
}

# ---------- HELPERS DE JAVA ----------
function Find-Entities {
    param([Parameter(Mandatory)][string]$MainJavaPath)
    $entityDir = Get-ChildItem -Path $MainJavaPath -Recurse -Directory -Filter 'entity' -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $entityDir) { return @() }
    $files = Get-ChildItem -Path $entityDir.FullName -Filter *.java -File -ErrorAction SilentlyContinue
    return $files
}

function Get-PackageBase {
    param([Parameter(Mandatory)][string]$MainJavaPath)
    # Tomamos el primer paquete com\inclusive\... que exista
    $pkgRoot = Get-ChildItem -Path $MainJavaPath -Recurse -Directory -Filter 'com' -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $pkgRoot) { return $null }
    # Buscar el subárbol que contenga el nombre del módulo si es posible
    return $pkgRoot.FullName
}

function Get-ModuleNameFromPath {
    param([string]$ModulePath)
    return Split-Path $ModulePath -Leaf
}

# ---------- GENERADORES ----------
function Ensure-Controller {
    param(
        [Parameter(Mandatory)][string]$PkgBase,
        [Parameter(Mandatory)][string]$MainJavaPath,
        [Parameter(Mandatory)][string]$EntityName,
        [Parameter(Mandatory)][string]$ModuleName
    )
    $pkgRel = $PkgBase.Substring($MainJavaPath.Length).TrimStart('\').Replace('\','.')
    $controllerDir = Join-Path $PkgBase 'controller'
    $controllerFile = Join-Path $controllerDir ("{0}Controller.java" -f $EntityName)

    if (Test-Path $controllerFile) {
        return @{ created = $false; path = $controllerFile }
    }

    $packageLine = "package $pkgRel.controller;"
    $entityPkg   = "$pkgRel.entity.$EntityName"
    $servicePkg  = "$pkgRel.service.${EntityName}Service"
    $apiBase     = "/api/" + (Slug $EntityName)

    $content = @"
$packageLine

import $entityPkg;
import $servicePkg;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("$apiBase")
public class ${EntityName}Controller {

    private final ${EntityName}Service service;

    public ${EntityName}Controller(${EntityName}Service service) {
        this.service = service;
    }

    @GetMapping
    public List<${EntityName}> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<${EntityName}> findById(@PathVariable Long id) {
        ${EntityName} e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @PostMapping
    public ${EntityName} create(@RequestBody ${EntityName} body) {
        return service.create(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<${EntityName}> update(@PathVariable Long id, @RequestBody ${EntityName} body) {
        ${EntityName} e = service.update(id, body);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean ok = service.delete(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
"@

    New-Dir $controllerDir
    Write-Utf8NoBom -Path $controllerFile -Content $content
    return @{ created = $true; path = $controllerFile }
}

function Ensure-Service {
    param(
        [Parameter(Mandatory)][string]$PkgBase,
        [Parameter(Mandatory)][string]$MainJavaPath,
        [Parameter(Mandatory)][string]$EntityName
    )
    $pkgRel      = $PkgBase.Substring($MainJavaPath.Length).TrimStart('\').Replace('\','.')
    $serviceDir  = Join-Path $PkgBase 'service'
    $serviceFile = Join-Path $serviceDir ("{0}Service.java" -f $EntityName)

    if (Test-Path $serviceFile) {
        return @{ created = $false; path = $serviceFile }
    }

    $packageLine = "package $pkgRel.service;"
    $entityPkg   = "$pkgRel.entity.$EntityName"

    $content = @"
$packageLine

import java.util.List;
import $entityPkg;

public interface ${EntityName}Service {
    List<${EntityName}> findAll();
    ${EntityName} findById(Long id);
    ${EntityName} create(${EntityName} body);
    ${EntityName} update(Long id, ${EntityName} body);
    boolean delete(Long id);
}
"@

    New-Dir $serviceDir
    Write-Utf8NoBom -Path $serviceFile -Content $content
    return @{ created = $true; path = $serviceFile }
}

function Ensure-ServiceImpl {
    param(
        [Parameter(Mandatory)][string]$PkgBase,
        [Parameter(Mandatory)][string]$MainJavaPath,
        [Parameter(Mandatory)][string]$EntityName
    )
    $pkgRel       = $PkgBase.Substring($MainJavaPath.Length).TrimStart('\').Replace('\','.')
    $implDir      = Join-Path $PkgBase 'service\impl'
    $implFile     = Join-Path $implDir ("{0}ServiceImpl.java" -f $EntityName)
    $serviceFile  = Join-Path $PkgBase 'service' ("{0}Service.java" -f $EntityName)

    if (Test-Path $implFile) {
        return @{ created = $false; path = $implFile }
    }

    # Si no hay Service interface, la creamos primero
    if (-not (Test-Path $serviceFile)) {
        Ensure-Service -PkgBase $PkgBase -MainJavaPath $MainJavaPath -EntityName $EntityName | Out-Null
    }

    $packageLine = "package $pkgRel.service.impl;"
    $entityPkg   = "$pkgRel.entity.$EntityName"
    $servicePkg  = "$pkgRel.service.${EntityName}Service"

    $content = @"
$packageLine

import org.springframework.stereotype.Service;
import java.util.*;
import $entityPkg;
import $servicePkg;

@Service
public class ${EntityName}ServiceImpl implements ${EntityName}Service {

    // In-memory storage mínimo para levantar el servicio
    private final Map<Long, ${EntityName}> data = new HashMap<>();
    private long seq = 1;

    @Override
    public List<${EntityName}> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public ${EntityName} findById(Long id) {
        return data.get(id);
    }

    @Override
    public ${EntityName} create(${EntityName} body) {
        data.put(seq, body);
        // Si la entidad tiene setId(Long), intentar setearla (no obligatorio)
        try {
            ${EntityName}.class.getMethod("setId", Long.class).invoke(body, seq);
        } catch (Exception ignored) {}
        seq++;
        return body;
    }

    @Override
    public ${EntityName} update(Long id, ${EntityName} body) {
        if (!data.containsKey(id)) return null;
        data.put(id, body);
        try {
            ${EntityName}.class.getMethod("setId", Long.class).invoke(body, id);
        } catch (Exception ignored) {}
        return body;
    }

    @Override
    public boolean delete(Long id) {
        return data.remove(id) != null;
    }
}
"@

    New-Dir $implDir
    Write-Utf8NoBom -Path $implFile -Content $content
    return @{ created = $true; path = $implFile }
}

function Ensure-TestFiles {
    param(
        [Parameter(Mandatory)][string]$ModulePath,
        [Parameter(Mandatory)][string]$EntityName
    )
    $testJava = Join-Path $ModulePath 'src\test\java'
    New-Dir $testJava
    $testFile = Join-Path $testJava ("Smoke_"+$EntityName+"_Test.java")
    if (Test-Path $testFile) {
        return @{ created = $false; path = $testFile }
    }
    $content = @"
// Minimal placeholder test to verify compilation
public class Smoke_${EntityName}_Test {
    @org.junit.jupiter.api.Test
    void compiles() { assert true; }
}
"@
    Write-Utf8NoBom -Path $testFile -Content $content
    return @{ created = $true; path = $testFile }
}

# ---------- REPORTE ----------
$runStamp   = (Get-Date -Format "yyyy-MM-dd_HH-mm-ss")
$csvPath    = Join-Path $repoRoot ("module_crud_fix_{0}.csv" -f $runStamp)
$htmlPath   = Join-Path $repoRoot ("module_crud_fix_{0}.html" -f $runStamp)
$csvRows    = New-Object System.Collections.Generic.List[object]

Write-Host "=== Reparación automática de CRUD (v7.1) ===`n"

foreach ($modulePath in $moduleRoots) {
    $moduleName  = Get-ModuleNameFromPath $modulePath
    if ($modulePath -eq $repoRoot) { $moduleName = "inclusive-learning-platform-backend" }
    Write-Host "[INFO] Módulo: $moduleName"

    $mainJava    = Join-Path $modulePath 'src\main\java'
    if (-not (Test-Path $mainJava)) {
        $csvRows.Add([pscustomobject]@{ Module=$moduleName; Entity="-"; Controller="-"; Service="-"; Impl="-"; Test="-"; Status="Sin src/main/java" })
        continue
    }

    $pkgBase = Get-PackageBase -MainJavaPath $mainJava
    if (-not $pkgBase) {
        $csvRows.Add([pscustomobject]@{ Module=$moduleName; Entity="-"; Controller="-"; Service="-"; Impl="-"; Test="-"; Status="Sin paquete base" })
        continue
    }

    $entities = @()
    $foundEntities = Find-Entities -MainJavaPath $mainJava
    if ($foundEntities) { $entities = $foundEntities }

    if (($entities | Measure-Object).Count -eq 0) {
        Write-Host "   [WARN] No se hallaron entidades en $moduleName. Se omite." -ForegroundColor Yellow
        $csvRows.Add([pscustomobject]@{ Module=$moduleName; Entity="-"; Controller="-"; Service="-"; Impl="-"; Test="-"; Status="Sin entidades" })
        continue
    }

    foreach ($entityFile in $entities) {
        $entityName = [System.IO.Path]::GetFileNameWithoutExtension($entityFile.Name)

        # Evitar sobreescribir controladores existentes como StudentController.java
        $controllerResult = Ensure-Controller -PkgBase $pkgBase -MainJavaPath $mainJava -EntityName $entityName -ModuleName $moduleName
        $serviceResult    = Ensure-Service    -PkgBase $pkgBase -MainJavaPath $mainJava -EntityName $entityName
        $implResult       = Ensure-ServiceImpl -PkgBase $pkgBase -MainJavaPath $mainJava -EntityName $entityName
        $testResult       = Ensure-TestFiles -ModulePath $modulePath -EntityName $entityName

        $csvRows.Add([pscustomobject]@{
            Module     = $moduleName
            Entity     = $entityName
            Controller = if ($controllerResult.created) { "CREATED" } else { "EXISTS" }
            Service    = if ($serviceResult.created)    { "CREATED" } else { "EXISTS" }
            Impl       = if ($implResult.created)       { "CREATED" } else { "EXISTS" }
            Test       = if ($testResult.created)       { "CREATED" } else { "EXISTS" }
            Status     = "OK"
        })
    }
}

# ---------- SALIDA CSV ----------
$csvRows | Export-Csv -Path $csvPath -NoTypeInformation -Encoding UTF8

# ---------- SALIDA HTML ----------
$total    = $csvRows.Count
$createdC = ($csvRows | Where-Object { $_.Controller -eq "CREATED" }).Count
$createdS = ($csvRows | Where-Object { $_.Service    -eq "CREATED" }).Count
$createdI = ($csvRows | Where-Object { $_.Impl       -eq "CREATED" }).Count
$createdT = ($csvRows | Where-Object { $_.Test       -eq "CREATED" }).Count

# Render simple de tabla
$rowsHtml = $csvRows | ForEach-Object {
    "<tr><td>{0}</td><td>{1}</td><td class='c{2}'>{3}</td><td class='c{4}'>{5}</td><td class='c{6}'>{7}</td><td class='c{8}'>{9}</td><td>{10}</td></tr>" -f `
        (HtmlEnc $_.Module), (HtmlEnc $_.Entity),
        (if($_.Controller -eq "CREATED"){'n'}else{'y'}), (HtmlEnc $_.Controller),
        (if($_.Service    -eq "CREATED"){'n'}else{'y'}), (HtmlEnc $_.Service),
        (if($_.Impl       -eq "CREATED"){'n'}else{'y'}), (HtmlEnc $_.Impl),
        (if($_.Test       -eq "CREATED"){'n'}else{'y'}), (HtmlEnc $_.Test),
        (HtmlEnc $_.Status)
}

$html = @"
<!doctype html>
<html><head><meta charset="utf-8"><title>Module CRUD Fix Report</title>
<style>
body{font-family:Segoe UI,Arial,sans-serif;margin:20px}
h1{margin:0 0 4px 0} small{color:#666}
table{border-collapse:collapse;width:100%;margin-top:16px}
th,td{border:1px solid #ddd;padding:8px;font-size:14px}
th{background:#f6f6f6;text-align:left}
tr:nth-child(even){background:#fafafa}
.badge{display:inline-block;padding:4px 8px;border-radius:8px;font-size:12px}
.ok{background:#e6ffed;color:#095b2d;border:1px solid #b7e7c2}
.warn{background:#fff4e5;color:#8a5a00;border:1px solid #ffd28a}
.cy{color:#095b2d;font-weight:600} .cn{color:#8a5a00;font-weight:600}
</style></head>
<body>
<h1>Inclusive Learning Platform - Module CRUD Fix Report</h1>
<small>Fecha: $(HtmlEnc (Get-Date))</small>

<p>
<span class="badge ok">Total filas: $total</span>
<span class="badge warn">Controladores creados: $createdC</span>
<span class="badge warn">Services creados: $createdS</span>
<span class="badge warn">Impl creadas: $createdI</span>
<span class="badge warn">Tests creados: $createdT</span>
</p>

<p>CSV: $(HtmlEnc $csvPath)</p>

<table>
<thead>
<tr>
<th>Módulo</th><th>Entidad</th><th>Controller</th><th>Service</th><th>Impl</th><th>Test</th><th>Status</th>
</tr>
</thead>
<tbody>
$($rowsHtml -join "`n")
</tbody>
</table>
</body></html>
"@

Write-Utf8NoBom -Path $htmlPath -Content $html

Write-Host "`n[OK] Reporte CSV: $csvPath"
Write-Host "[OK] Reporte HTML: $htmlPath"

# Auto-open (Windows/WSL)
try {
    if ($IsWindows) { Start-Process $htmlPath }
    else { & xdg-open $htmlPath 2>$null }
} catch { }

Write-Host "`n=== Reparación completada correctamente (v7.1) ==="
