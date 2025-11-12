# repair-crud-modules-v7.ps1
# Backend CRUD auto-repair + Swagger + Tests + HTML report (safe/append-only)
# Ejecutar desde la raíz del repo

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

# -------------------------
# Helpers
# -------------------------
function New-DirSafe {
    param([Parameter(Mandatory=$true)][string]$Path)
    if (-not (Test-Path -LiteralPath $Path)) {
        New-Item -ItemType Directory -Path $Path -Force | Out-Null
    }
}

function Write-Utf8NoBom {
    param(
        [Parameter(Mandatory=$true)][string]$Path,
        [Parameter(Mandatory=$true)][string]$Content
    )
    New-DirSafe -Path (Split-Path -LiteralPath $Path -Parent)
    $bytes = [System.Text.UTF8Encoding]::new($false).GetBytes($Content)
    [System.IO.File]::WriteAllBytes($Path, $bytes)
}

function HtmlEncode {
    param([string]$s)
    return [System.Net.WebUtility]::HtmlEncode($s)
}

function Detect-Modules {
    # Módulos con estructura Maven típica
    $candidates = @(
        "adaptive-education-service",
        "assessment-service",
        "auth-service",
        "commons-service",
        "gateway-service",
        "monitoring-service",
        "notification-service",
        "report-service",
        "tenant-service"
    )

    $found = @()
    foreach ($m in $candidates) {
        if (Test-Path -LiteralPath $m) {
            $found += (Resolve-Path -LiteralPath $m).Path
        }
    }

    # Además, si hay un módulo raíz estilo monolito (src/main/java)
    if (Test-Path -LiteralPath 'src/main/java') {
        $found += (Resolve-Path -LiteralPath '.').Path
    }

    return $found | Sort-Object -Unique
}

function Get-JavaRoots {
    param([string]$modulePath)

    $mainJava = Join-Path $modulePath 'src\main\java'
    $testJava = Join-Path $modulePath 'src\test\java'
    return ,@{
        Main = $mainJava
        Test = $testJava
    }
}

function Guess-PackageBase {
    param(
        [string]$mainJavaPath,
        [string]$moduleName
    )
    # Intenta leer el primer package existente en el módulo
    $pkg = $null
    if (Test-Path -LiteralPath $mainJavaPath) {
        $someJava = Get-ChildItem -LiteralPath $mainJavaPath -Recurse -Filter *.java -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($someJava) {
            $line = (Get-Content -LiteralPath $someJava.FullName -ErrorAction SilentlyContinue | Select-String -Pattern '^\s*package\s+([a-zA-Z0-9\._]+)\s*;' | Select-Object -First 1)
            if ($line) {
                $pkg = $line.Matches.Groups[1].Value
                # recorta hasta com.inclusive.*
                if ($pkg -match '^(com\.inclusive(\.[a-z0-9_]+)*)') {
                    $pkg = $Matches[1]
                }
            }
        }
    }
    if (-not $pkg) {
        # fallback -> com.inclusive.<module_sin_guiones>
        $short = ($moduleName -replace '[^a-zA-Z0-9]', '').ToLower()
        if ([string]::IsNullOrWhiteSpace($short)) { $short = "platform" }
        $pkg = "com.inclusive.$short"
    }
    return $pkg
}

function Find-Entities {
    param([string]$mainJavaPath)

    $entities = @()
    if (Test-Path -LiteralPath $mainJavaPath) {
        $entityDirs = Get-ChildItem -LiteralPath $mainJavaPath -Recurse -Directory -ErrorAction SilentlyContinue |
                      Where-Object { $_.FullName -match '\\entity($|\\)' }
        foreach ($dir in $entityDirs) {
            $files = Get-ChildItem -LiteralPath $dir.FullName -Filter *.java -ErrorAction SilentlyContinue
            foreach ($f in $files) {
                # Nombre entidad por nombre de archivo
                $name = [System.IO.Path]::GetFileNameWithoutExtension($f.Name)
                if ($name -and $name -notmatch 'Test$') {
                    $entities += [PSCustomObject]@{
                        Name = $name
                        Dir  = $dir.FullName
                        File = $f.FullName
                    }
                }
            }
        }
    }
    return $entities
}

function Build-PackagePaths {
    param(
        [string]$pkgBase,
        [string]$mainJava,
        [string]$testJava
    )
    $pkgPath = $pkgBase -replace '\.', '\'
    return ,@{
        Controller = Join-Path $mainJava (Join-Path $pkgPath 'controller')
        Service    = Join-Path $mainJava (Join-Path $pkgPath 'service')
        Impl       = Join-Path $mainJava (Join-Path (Join-Path $pkgPath 'service') 'impl')
        Test       = Join-Path $testJava (Join-Path $pkgPath 'controller')
    }
}

# -------------------------
# Java Templates (placeholders: __PKG__, __ENTITY__)
# -------------------------

$ControllerTemplate = @'
package __PKG__.controller;

import __PKG__.service.__ENTITY__Service;
import __PKG__.entity.__ENTITY__;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/__entity__")
@Tag(name="__ENTITY__ API", description="CRUD operations for __ENTITY__")
public class __ENTITY__Controller {

    private final __ENTITY__Service service;

    public __ENTITY__Controller(__ENTITY__Service service) {
        this.service = service;
    }

    @Operation(summary="List all __ENTITY__")
    @GetMapping
    public ResponseEntity<List<__ENTITY__>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary="Get __ENTITY__ by id")
    @GetMapping("/{id}")
    public ResponseEntity<__ENTITY__> findById(@PathVariable Long id) {
        __ENTITY__ e = service.findById(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @Operation(summary="Create __ENTITY__")
    @PostMapping
    public ResponseEntity<__ENTITY__> create(@RequestBody __ENTITY__ body) {
        return ResponseEntity.ok(service.create(body));
    }

    @Operation(summary="Update __ENTITY__")
    @PutMapping("/{id}")
    public ResponseEntity<__ENTITY__> update(@PathVariable Long id, @RequestBody __ENTITY__ body) {
        __ENTITY__ updated = service.update(id, body);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @Operation(summary="Delete __ENTITY__")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
'@

$ServiceTemplate = @'
package __PKG__.service;

import __PKG__.entity.__ENTITY__;
import java.util.List;

public interface __ENTITY__Service {
    List<__ENTITY__> findAll();
    __ENTITY__ findById(Long id);
    __ENTITY__ create(__ENTITY__ e);
    __ENTITY__ update(Long id, __ENTITY__ e);
    boolean delete(Long id);
}
'@

$ServiceImplTemplate = @'
package __PKG__.service.impl;

import __PKG__.service.__ENTITY__Service;
import __PKG__.entity.__ENTITY__;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class __ENTITY__ServiceImpl implements __ENTITY__Service {

    private final Map<Long, __ENTITY__> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public List<__ENTITY__> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public __ENTITY__ findById(Long id) {
        return store.get(id);
    }

    @Override
    public __ENTITY__ create(__ENTITY__ e) {
        long id = seq.getAndIncrement();
        try {
            // intentar setId si existe
            e.getClass().getMethod("setId", Long.class).invoke(e, id);
        } catch (Exception ignore) {}
        store.put(id, e);
        return e;
    }

    @Override
    public __ENTITY__ update(Long id, __ENTITY__ e) {
        if (!store.containsKey(id)) return null;
        try {
            e.getClass().getMethod("setId", Long.class).invoke(e, id);
        } catch (Exception ignore) {}
        store.put(id, e);
        return e;
    }

    @Override
    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}
'@

$TestTemplate = @'
package __PKG__.controller;

import __PKG__.entity.__ENTITY__;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class __ENTITY__ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() throws Exception {
        mockMvc.perform(get("/api/__entity__"))
                .andExpect(status().isOk());
    }
}
'@

# -------------------------
# Main
# -------------------------
Write-Host "=== Reparación automática de CRUD (v7) ===`n"

$modules = Detect-Modules
if (-not $modules -or $modules.Count -eq 0) {
    Write-Host "[WARN] No se detectaron módulos. ¿Estás en la raíz del repo?" -ForegroundColor Yellow
    exit 0
}

$report = New-Object System.Collections.Generic.List[object]

foreach ($modulePath in $modules) {
    $moduleName = Split-Path -Leaf $modulePath
    Write-Host "[INFO] Módulo: $moduleName"

    $roots = Get-JavaRoots -modulePath $modulePath
    $mainJava = $roots.Main
    $testJava = $roots.Test

    if (-not (Test-Path -LiteralPath $mainJava)) {
        Write-Host "   [SKIP] No hay src/main/java en $moduleName" -ForegroundColor DarkGray
        continue
    }

    New-DirSafe -Path $mainJava
    New-DirSafe -Path $testJava

    $pkgBase = Guess-PackageBase -mainJavaPath $mainJava -moduleName $moduleName
    $paths = Build-PackagePaths -pkgBase $pkgBase -mainJava $mainJava -testJava $testJava

    New-DirSafe -Path $paths.Controller
    New-DirSafe -Path $paths.Service
    New-DirSafe -Path $paths.Impl
    New-DirSafe -Path $paths.Test

    $entities = @()  # <--- inicialización segura
$foundEntities = Find-Entities -mainJavaPath $mainJava
if ($foundEntities) { $entities = $foundEntities }

if (($entities | Measure-Object).Count -eq 0) {
    Write-Host "   [WARN] No se hallaron entidades en $moduleName. Se omite." -ForegroundColor Yellow
    continue
}



    foreach ($entity in $entities) {
        $entityName = $entity.Name
        $entityLower = $entityName.Substring(0,1).ToLower() + $entityName.Substring(1)

        $controllerFile = Join-Path $paths.Controller "$($entityName)Controller.java"
        $serviceFile    = Join-Path $paths.Service    "$($entityName)Service.java"
        $implFile       = Join-Path $paths.Impl       "$($entityName)ServiceImpl.java"
        $testFile       = Join-Path $paths.Test       "$($entityName)ControllerTest.java"

        $created = @()

        if (-not (Test-Path -LiteralPath $controllerFile)) {
            $content = $ControllerTemplate.Replace('__PKG__', $pkgBase).Replace('__ENTITY__', $entityName).Replace('__entity__', $entityLower)
            Write-Utf8NoBom -Path $controllerFile -Content $content
            $created += 'Controller'
        }

        if (-not (Test-Path -LiteralPath $serviceFile)) {
            $content = $ServiceTemplate.Replace('__PKG__', $pkgBase).Replace('__ENTITY__', $entityName)
            Write-Utf8NoBom -Path $serviceFile -Content $content
            $created += 'Service'
        }

        if (-not (Test-Path -LiteralPath $implFile)) {
            $content = $ServiceImplTemplate.Replace('__PKG__', $pkgBase).Replace('__ENTITY__', $entityName)
            Write-Utf8NoBom -Path $implFile -Content $content
            $created += 'ServiceImpl'
        }

        if (-not (Test-Path -LiteralPath $testFile)) {
            $content = $TestTemplate.Replace('__PKG__', $pkgBase).Replace('__ENTITY__', $entityName).Replace('__entity__', $entityLower)
            Write-Utf8NoBom -Path $testFile -Content $content
            $created += 'Test'
        }

        $status = if ($created.Count -eq 0) { 'Completo' } else { 'Actualizado: ' + ($created -join ', ') }

        $report.Add([PSCustomObject]@{
            Module      = $moduleName
            PackageBase = $pkgBase
            Entity      = $entityName
            Controller  = (Test-Path -LiteralPath $controllerFile)
            Service     = (Test-Path -LiteralPath $serviceFile)
            Impl        = (Test-Path -LiteralPath $implFile)
            Test        = (Test-Path -LiteralPath $testFile)
            Status      = $status
            ControllerPath = $controllerFile
            ServicePath    = $serviceFile
            ImplPath       = $implFile
            TestPath       = $testFile
        })
    }
}

# -------------------------
# Reportes
# -------------------------
$ts = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$csvPath  = "module_crud_fix_$ts.csv"
$htmlPath = "module_crud_fix_$ts.html"

$report | Export-Csv -Path $csvPath -NoTypeInformation -Encoding UTF8

# HTML
$rows = foreach ($r in $report) {
    $cls = switch ($r.Status) {
        'Completo' { 'ok' }
        default    { 'upd' }
    }
    "<tr class='$cls'><td>$(HtmlEncode $r.Module)</td><td>$(HtmlEncode $r.Entity)</td><td>$($r.Controller)</td><td>$($r.Service)</td><td>$($r.Impl)</td><td>$($r.Test)</td><td>$(HtmlEncode $r.Status)</td><td>$(HtmlEncode $r.ControllerPath)</td></tr>"
}

$html = @"
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8"/>
<title>Inclusive Learning Platform - CRUD Report</title>
<style>
 body{font-family:Segoe UI,Arial,Helvetica,sans-serif;margin:20px;}
 h1{margin:0 0 10px 0}
 table{border-collapse:collapse;width:100%}
 th,td{border:1px solid #ddd;padding:8px;font-size:14px}
 th{background:#f4f4f4;text-align:left}
 tr.ok td{background:#f7fff7}
 tr.upd td{background:#fffcef}
 .summary{margin:10px 0 20px 0}
 code{background:#f2f2f2;padding:2px 4px;border-radius:3px}
</style>
</head>
<body>
<h1>Inclusive Learning Platform - Module CRUD Report</h1>
<div class="summary">
  <div>Fecha: $(HtmlEncode $ts)</div>
  <div>CSV: <code>$(HtmlEncode $csvPath)</code></div>
</div>
<table>
  <thead>
    <tr>
      <th>Módulo</th><th>Entidad</th><th>Controller</th><th>Service</th><th>Impl</th><th>Test</th><th>Estado</th><th>Controller Path</th>
    </tr>
  </thead>
  <tbody>
    $(($rows -join "`n"))
  </tbody>
</table>
</body>
</html>
"@

Write-Utf8NoBom -Path $htmlPath -Content $html

Write-Host "`n[OK] Reporte CSV: $csvPath"
Write-Host "[OK] Reporte HTML: $htmlPath`n"

# Abre el HTML en el navegador
try {
    Start-Process $htmlPath | Out-Null
} catch {
    Write-Host "[WARN] No se pudo abrir el HTML automáticamente. Ábrelo manualmente: $htmlPath" -ForegroundColor Yellow
}

Write-Host "=== Reparación completada (v7) ==="
