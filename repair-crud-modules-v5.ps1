<# 
  repair-crud-modules-v5.ps1
  - No sobrescribe archivos existentes.
  - Busca módulos con 'src/main/java'.
  - Detecta Entities (@Entity) y Services (interface *Service).
  - Crea controladores CRUD faltantes por Entity.
  - Si no hay Service, genera endpoints stub (501).
  - Reporte CSV: created_controllers_<timestamp>.csv
#>

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Write-Info($msg){ Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-Ok($msg){ Write-Host "[OK]   $msg" -ForegroundColor Green }
function Write-Warn($msg){ Write-Host "[WARN] $msg" -ForegroundColor Yellow }
function Write-Err($msg){ Write-Host "[ERR]  $msg" -ForegroundColor Red }

$repoRoot = (Get-Location).Path
$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm"
$report = New-Object System.Collections.Generic.List[object]

Write-Host "=== Reparación automática de CRUD (v5) ===" -ForegroundColor Magenta

# 1) Localiza módulos Java (carpetas que contienen src/main/java)
$moduleRoots = Get-ChildItem -Directory -Recurse -ErrorAction SilentlyContinue |
    Where-Object { Test-Path (Join-Path $_.FullName 'src\main\java') } |
    # Evita directorios target/ y legacy backups
    Where-Object { $_.FullName -notmatch '\\target\\' -and $_.FullName -notmatch 'legacy_backup' } |
    # Relevantes: *-service y src de raíz
    Sort-Object FullName -Unique

if(-not $moduleRoots){
    Write-Warn "No se encontraron módulos con 'src/main/java'. Nada por hacer."
    exit 0
}

# Utilidad: detecta paquete base a partir de un archivo de ejemplo
function Get-PackageFromFile {
    param([string]$javaFile)
    try{
        $pkgLine = Select-String -Path $javaFile -Pattern '^\s*package\s+([a-zA-Z0-9_.]+)\s*;' -SimpleMatch:$false -ErrorAction SilentlyContinue | Select-Object -First 1
        if($pkgLine){
            $m = [regex]::Match($pkgLine.Line, 'package\s+([a-zA-Z0-9_.]+)\s*;')
            if($m.Success){ return $m.Groups[1].Value }
        }
    }catch{}
    return $null
}

# Utilidad: obtiene paquete base del módulo (buscando en controllers, entities o services)
function Detect-BasePackage {
    param([string]$moduleJavaRoot)

    # Priorizar controllers, luego entities, luego services, luego cualquier .java
    $candidates = @(
        Get-ChildItem -Path (Join-Path $moduleJavaRoot 'controller') -Recurse -Filter *.java -ErrorAction SilentlyContinue,
        Get-ChildItem -Path (Join-Path $moduleJavaRoot 'entity') -Recurse -Filter *.java -ErrorAction SilentlyContinue,
        Get-ChildItem -Path (Join-Path $moduleJavaRoot 'service') -Recurse -Filter *.java -ErrorAction SilentlyContinue,
        Get-ChildItem -Path $moduleJavaRoot -Recurse -Filter *.java -ErrorAction SilentlyContinue
    ) | Where-Object { $_ } | Select-Object -First 1

    if($candidates){
        $pkg = Get-PackageFromFile -javaFile $candidates.FullName
        if($pkg){ return $pkg }
    }
    # Fallback: construir desde la ruta
    return 'com.inclusive.' + [IO.Path]::GetFileName([IO.Path]::GetDirectoryName($moduleJavaRoot))
}

# Utilidad: extrae nombre de clase pública de un archivo .java
function Get-PublicClassName {
    param([string]$javaFile)
    try{
        $m = Select-String -Path $javaFile -Pattern 'public\s+(class|interface)\s+([A-Za-z0-9_]+)' -ErrorAction SilentlyContinue | Select-Object -First 1
        if($m){
            $mm = [regex]::Match($m.Line, 'public\s+(class|interface)\s+([A-Za-z0-9_]+)')
            if($mm.Success){ return $mm.Groups[2].Value }
        }
    }catch{}
    return $null
}

# Utilidad: transforma paquete dot a ruta
function PackageToPath {
    param([string]$pkg)
    return ($pkg -replace '\.', [IO.Path]::DirectorySeparatorChar)
}

# Utilidad: crea archivo si no existe
function Safe-WriteFile {
    param(
        [string]$Path,
        [string]$Content
    )
    if(Test-Path $Path){
        Write-Warn "Ya existe: $Path (no se sobrescribe)"
        return $false
    }
    $dir = Split-Path $Path -Parent
    if(-not (Test-Path $dir)){ New-Item -ItemType Directory -Path $dir | Out-Null }
    $Content | Out-File -FilePath $Path -Encoding UTF8 -Force
    Write-Ok "Creado: $Path"
    return $true
}

# Plantilla Controller con Service
$ControllerWithService = @'
package {0}.controller;

import {0}.entity.{1};
import {0}.service.{1}Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/{2}")
@Tag(name = "{1} API")
public class {1}Controller {{

    private final {1}Service service;

    public {1}Controller({1}Service service) {{
        this.service = service;
    }}

    @GetMapping
    @Operation(summary = "Listar todos")
    public ResponseEntity<List<{1}>> findAll() {{
        return ResponseEntity.ok(service.findAll());
    }}

    @GetMapping("/{id}")
    @Operation(summary = "Obtener por Id")
    public ResponseEntity<{1}> findById(@PathVariable Long id) {{
        return ResponseEntity.of(service.findById(id));
    }}

    @PostMapping
    @Operation(summary = "Crear")
    public ResponseEntity<{1}> create(@RequestBody {1} body) {{
        return ResponseEntity.ok(service.create(body));
    }}

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar")
    public ResponseEntity<{1}> update(@PathVariable Long id, @RequestBody {1} body) {{
        return ResponseEntity.ok(service.update(id, body));
    }}

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar")
    public ResponseEntity<Void> delete(@PathVariable Long id) {{
        service.delete(id);
        return ResponseEntity.noContent().build();
    }}
}}
'@

# Plantilla Controller STUB (sin Service)
$ControllerStub = @'
package {0}.controller;

import {0}.entity.{1};
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/{2}")
@Tag(name = "{1} API (stub)")
public class {1}Controller {{

    @GetMapping
    @Operation(summary = "Listar todos (stub)")
    public ResponseEntity<List<{1}>> findAll() {{
        return ResponseEntity.status(501).body(Collections.emptyList());
    }}

    @GetMapping("/{id}")
    @Operation(summary = "Obtener por Id (stub)")
    public ResponseEntity<{1}> findById(@PathVariable Long id) {{
        return ResponseEntity.status(501).build();
    }}

    @PostMapping
    @Operation(summary = "Crear (stub)")
    public ResponseEntity<{1}> create(@RequestBody {1} body) {{
        return ResponseEntity.status(501).build();
    }}

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar (stub)")
    public ResponseEntity<{1}> update(@PathVariable Long id, @RequestBody {1} body) {{
        return ResponseEntity.status(501).build();
    }}

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (stub)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {{
        return ResponseEntity.status(501).build();
    }}
}}
'@

# Recorre cada módulo
foreach($moduleDir in $moduleRoots){
    $modulePath = $moduleDir.FullName
    $javaRoot = Join-Path $modulePath 'src\main\java'
    if(-not (Test-Path $javaRoot)){ continue }

    $moduleName = Split-Path $modulePath -Leaf
    Write-Info "Módulo: $moduleName"

    # Detecta paquete base
    $basePackage = Detect-BasePackage -moduleJavaRoot $javaRoot
    $packagePath = PackageToPath -pkg $basePackage
    $controllerDir = Join-Path $javaRoot (Join-Path $packagePath 'controller')
    if(-not (Test-Path $controllerDir)){ New-Item -ItemType Directory -Path $controllerDir | Out-Null }

    # Detecta Entities (@Entity)
    $entityFiles = Get-ChildItem -Path (Join-Path $javaRoot 'entity') -Recurse -Filter *.java -ErrorAction SilentlyContinue |
                   Where-Object { (Select-String -Path $_.FullName -Pattern '@Entity' -SimpleMatch -ErrorAction SilentlyContinue) }

    if(-not $entityFiles){
        Write-Warn "No se encontraron @Entity en $moduleName. Se omite."
        continue
    }

    # Detecta Services (interfaces) para saber si el controlador puede inyectarlos
    $serviceFiles = Get-ChildItem -Path (Join-Path $javaRoot 'service') -Recurse -Filter *Service.java -ErrorAction SilentlyContinue
    $serviceNames = @{}
    foreach($sf in $serviceFiles){
        $svcName = Get-PublicClassName -javaFile $sf.FullName
        if($svcName){ $serviceNames[$svcName] = $sf.FullName }
    }

    foreach($ef in $entityFiles){
        $entityName = Get-PublicClassName -javaFile $ef.FullName
        if(-not $entityName){ continue }

        # Determina si existe servicio {Entity}Service
        $expectedService = "${entityName}Service"
        $hasService = $serviceNames.ContainsKey($expectedService)

        # Ruta de Controller
        $controllerFile = Join-Path $controllerDir ("{0}Controller.java" -f $entityName)

        if(Test-Path $controllerFile){
            Write-Ok "Controller existente (no se toca): $($controllerFile)"
            $report.Add([pscustomobject]@{
                Module = $moduleName; Entity=$entityName; ServiceDetected=$hasService; Action='Skipped'; Path=$controllerFile
            })
            continue
        }

        # Construye el path del paquete de la entity real, por si el paquete base difiere
        $entityPkg = Get-PackageFromFile -javaFile $ef.FullName
        if($entityPkg -and $entityPkg -ne "$basePackage.entity"){
            # Ajusta basePackage a lo que precede a ".entity"
            if($entityPkg -match '^(.*)\.entity$'){ $basePackage = $Matches[1]; $packagePath = PackageToPath -pkg $basePackage; $controllerDir = Join-Path $javaRoot (Join-Path $packagePath 'controller'); if(-not (Test-Path $controllerDir)){ New-Item -ItemType Directory -Path $controllerDir | Out-Null }; $controllerFile = Join-Path $controllerDir ("{0}Controller.java" -f $entityName) }
        }

        # Ruta base REST: plural simple en minúsculas
        $restBase = ($entityName).ToLower() + "s"

        if($hasService){
            $code = [string]::Format($ControllerWithService, $basePackage, $entityName, $restBase)
        } else {
            $code = [string]::Format($ControllerStub, $basePackage, $entityName, $restBase)
        }

        $created = Safe-WriteFile -Path $controllerFile -Content $code
        if($created){
            $report.Add([pscustomobject]@{
                Module = $moduleName; Entity=$entityName; ServiceDetected=$hasService; Action='Created'; Path=$controllerFile
            })
        } else {
            $report.Add([pscustomobject]@{
                Module = $moduleName; Entity=$entityName; ServiceDetected=$hasService; Action='Exists'; Path=$controllerFile
            })
        }
    }
}

#  Reporte CSV
$csvPath = Join-Path $repoRoot ("created_controllers_{0}.csv" -f $timestamp)
$report | Export-Csv -Path $csvPath -Encoding UTF8 -NoTypeInformation
Write-Ok "Reporte: $csvPath"

Write-Host "=== Finalizado ===" -ForegroundColor Magenta
