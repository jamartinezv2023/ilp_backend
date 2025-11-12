# repair-crud-modules-v11.1.ps1
# Versión: v11.1
# Objetivo: Escanear recursivamente un proyecto multi-módulo Maven, detectar entidades Java y generar/repair CRUD (Controller, Service, ServiceImpl, Repository, DTO, Model) para cada entidad.
# Mejoras v11.1 respecto a v11:
#  - Validación robusta de rutas para evitar errores con Join-Path y variables vacías.
#  - README.md por módulo SIEMPRE generado (sobrescribe) y contiene fecha/hora de generación.
#  - Añadido campo ModulePath en los resúmenes para crear índice global correctamente.
#  - Pequeñas correcciones en manejo de pom.xml y encoding.
#
# USO:
# Guardar en la raíz del proyecto inclusive-learning-platform-backend y ejecutar:
#   pwsh -File .\repair-crud-modules-v11.1.ps1
# o en PowerShell (Windows):
#   .\repair-crud-modules-v11.1.ps1

# ------------------------------------------------------------
# Configuración inicial y funciones utilitarias
# ------------------------------------------------------------
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

function Write-Utf8NoBom {
    param(
        [Parameter(Mandatory=$true)][string]$Path,
        [Parameter(Mandatory=$true)][string]$Content
    )
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    $dir = Split-Path $Path -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [System.IO.File]::WriteAllText($Path, $Content, $utf8NoBom)
}

function Safe-WriteFile {
    param(
        [string]$Path,
        [string]$Content,
        [switch]$ForceOverwrite
    )
    if (Test-Path $Path) {
        $existing = Get-Content $Path -Raw -ErrorAction SilentlyContinue
        if ($existing -ne $Content) {
            if ($ForceOverwrite) {
                Write-Utf8NoBom -Path $Path -Content $Content
                return 'Updated'
            } else {
                return 'Exists'
            }
        } else {
            return 'Unchanged'
        }
    } else {
        Write-Utf8NoBom -Path $Path -Content $Content
        return 'Created'
    }
}

function Csv-Escape([string]$s){ if ($null -eq $s) { return '' } $s = $s -replace '"','""'; return '"' + $s + '"' }

# ------------------------------------------------------------
# Parámetros / rutas
# ------------------------------------------------------------
$ScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
if (-not $ScriptRoot) { $ScriptRoot = (Get-Location).Path }
$BasePath = $ScriptRoot
$ReportsDir = Join-Path $BasePath 'reports'
if (-not (Test-Path $ReportsDir)) { New-Item -ItemType Directory -Path $ReportsDir | Out-Null }

# Archivo de reporte CSV global
$csvReport = Join-Path $ReportsDir 'report_endpoints.csv'
$htmlReport = Join-Path $ReportsDir 'report_endpoints.html'
$globalRows = @()

Write-Host "[INFO] Escaneando proyecto en: $BasePath" -ForegroundColor Cyan

# ------------------------------------------------------------
# Encontrar todos los archivos Java recursivamente (ignorar target)
# ------------------------------------------------------------
$javaFiles = Get-ChildItem -Path $BasePath -Recurse -Filter *.java -ErrorAction SilentlyContinue | Where-Object { -not ($_.FullName -match '\\target\\') }
Write-Host "[INFO] Archivos .java encontrados: $($javaFiles.Count)" -ForegroundColor Green

# ------------------------------------------------------------
# Detectar módulos (carpetas que contienen src/main/java)
# ------------------------------------------------------------
$moduleRoots = @()
$possibleRoots = Get-ChildItem -Path $BasePath -Directory -ErrorAction SilentlyContinue
foreach ($d in $possibleRoots) {
    $check = Join-Path $d.FullName 'src\main\java'
    if (Test-Path $check) { $moduleRoots += $d.FullName }
}
# Asegurar incluir la raiz si tiene .java directamente
if ($javaFiles | Where-Object { $_.DirectoryName -eq $BasePath }) { $moduleRoots += $BasePath }
$moduleRoots = $moduleRoots | Sort-Object -Unique

if ($moduleRoots.Count -eq 0) {
    Write-Host "[WARN] No se encontraron módulos con src/main/java. El script procederá escaneando todo el proyecto." -ForegroundColor Yellow
    $moduleRoots = @($BasePath)
}

Write-Host "[INFO] Módulos detectados: $($moduleRoots.Count)" -ForegroundColor Cyan

# ------------------------------------------------------------
# Plantillas para archivos Java (simplificadas pero funcionales)
# ------------------------------------------------------------
function Template-Repository($package, $entityName) {
    return @"
package $package.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import $package.model.$entityName;

@Repository
public interface ${entityName}Repository extends JpaRepository<$entityName, Long> {
}
"@
}

function Template-DTO($package, $entityName) {
    return @"
package $package.dto;

public class ${entityName}DTO {
    private Long id;
    // TODO: agregar campos del DTO (copiar desde entidad si es necesario)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
"@
}

function Template-Service($package, $entityName) {
    return @"
package $package.service;

import java.util.List;
import $package.model.$entityName;

public interface ${entityName}Service {
    List<$entityName> listAll();
    $entityName getById(Long id);
    $entityName create($entityName entity);
    $entityName update(Long id, $entityName entity);
    void delete(Long id);
}
"@
}

function Template-ServiceImpl($package, $entityName) {
    return @"
package $package.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import $package.model.$entityName;
import $package.repository.${entityName}Repository;
import $package.service.${entityName}Service;

@Service
public class ${entityName}ServiceImpl implements ${entityName}Service {

    @Autowired
    private ${entityName}Repository repository;

    @Override
    public List<$entityName> listAll() { return repository.findAll(); }

    @Override
    public $entityName getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public $entityName create($entityName entity) { return repository.save(entity); }

    @Override
    public $entityName update(Long id, $entityName entity) {
        Optional<$entityName> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}
"@
}

function Template-Controller($package, $entityName, $basePathApi) {
    return @"
package $package.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import $package.model.$entityName;
import $package.service.${entityName}Service;

@RestController
@RequestMapping("/api/$basePathApi")
public class ${entityName}Controller {

    @Autowired
    private ${entityName}Service service;

    @GetMapping("/list")
    public ResponseEntity<List<$entityName>> list() { return ResponseEntity.ok(service.listAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<$entityName> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }

    @PostMapping
    public ResponseEntity<$entityName> create(@RequestBody $entityName entity) { return ResponseEntity.ok(service.create(entity)); }

    @PutMapping("/{id}")
    public ResponseEntity<$entityName> update(@PathVariable Long id, @RequestBody $entityName entity) { return ResponseEntity.ok(service.update(id, entity)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
"@ -replace "\r\n\r\n","\r\n"
}

function Template-Model($package, $entityName, $fieldsComment) {
    return @"
package $package.model;

import javax.persistence.*;

@Entity
public class $entityName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos detectados (manual):
    // $fieldsComment

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
"@ -replace "\r\n\r\n","\r\n"
}

# ------------------------------------------------------------
# Plantilla para pom.xml si no existe (simple y segura)
# ------------------------------------------------------------
function Template-Pom($artifactId, $groupId) {
    return @"
<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">
  <modelVersion>4.0.0</modelVersion>
  <groupId>$groupId</groupId>
  <artifactId>$artifactId</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <properties>
    <java.version>17</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
          <executable>true</executable>
          <jvmArguments>-Dfile.encoding=UTF8</jvmArguments>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
"@
}

# ------------------------------------------------------------
# Procesar cada módulo: detectar entidades y generar CRUD
# ------------------------------------------------------------
$moduleSummaries = @()
foreach ($moduleRoot in $moduleRoots) {
    Write-Host "[INFO] Procesando módulo: $moduleRoot" -ForegroundColor Cyan
    $moduleJavaRoot = Join-Path $moduleRoot 'src\main\java'
    if (-not (Test-Path $moduleJavaRoot)) { # permitir que también funcione si .java está en la raiz del módulo
        $moduleJavaRoot = $moduleRoot
    }

    # recolectar todas las clases con @Entity en el módulo
    $entityFiles = @()
    if (Test-Path $moduleJavaRoot) {
        $entityFiles = Get-ChildItem -Path $moduleJavaRoot -Recurse -Filter *.java -ErrorAction SilentlyContinue | Where-Object { (Get-Content $_.FullName -Raw -ErrorAction SilentlyContinue) -match '@Entity' }
    }
    Write-Host "[INFO] Entidades encontradas en módulo: $($entityFiles.Count)" -ForegroundColor Green

    $moduleReport = [ordered]@{
        Module = Split-Path $moduleRoot -Leaf;
        ModulePath = $moduleRoot;
        Entities = @()
    }

    foreach ($f in $entityFiles) {
        $full = $f.FullName
        $text = Get-Content $full -Raw -ErrorAction SilentlyContinue
        # Extraer package y nombre de clase
        $pkg = ''
        if ($text -match 'package\s+([\w\.]+)\s*;') { $pkg = $Matches[1] }
        $className = [IO.Path]::GetFileNameWithoutExtension($full)
        Write-Host "[INFO] Generando CRUD para entidad: $className" -ForegroundColor DarkGreen

        # construir paquete base (sin .model si existe)
        if ($pkg) { $basePackage = $pkg -replace '\.model$','' } else { $basePackage = 'com.generated' }

        # Rutas destino
        $packagePath = $basePackage -replace '\.','\\'
        $destModelDir = Join-Path $moduleJavaRoot ($packagePath + '\model')
        $destRepoDir = Join-Path $moduleJavaRoot ($packagePath + '\repository')
        $destServiceDir = Join-Path $moduleJavaRoot ($packagePath + '\service')
        $destServiceImplDir = Join-Path $moduleJavaRoot ($packagePath + '\service\\impl')
        $destControllerDir = Join-Path $moduleJavaRoot ($packagePath + '\controller')
        $destDtoDir = Join-Path $moduleJavaRoot ($packagePath + '\dto')

        # Asegurar existencia
        foreach ($d in @($destModelDir,$destRepoDir,$destServiceDir,$destServiceImplDir,$destControllerDir,$destDtoDir)) { if (-not (Test-Path $d)) { New-Item -ItemType Directory -Path $d -Force | Out-Null } }

        # Detectar campos simples para comentarios (buscar private )
        $fields = @()
        foreach ($line in ($text -split "\r?\n")) {
            if ($line -match 'private\s+[\w<>\[\]]+\s+(\w+)\s*;') { $fields += $Matches[1] }
        }
        $fieldsComment = ($fields -join ', ')

        # Generar archivos
        $repoContent = Template-Repository -package $basePackage -entityName $className
        $dtoContent = Template-DTO -package $basePackage -entityName $className
        $serviceContent = Template-Service -package $basePackage -entityName $className
        $serviceImplContent = Template-ServiceImpl -package $basePackage -entityName $className
        $controllerContent = Template-Controller -package $basePackage -entityName $className -basePathApi ($className.ToLower())
        # Si la entidad no tiene package, crear model con package base
        $modelContent = Template-Model -package $basePackage -entityName $className -fieldsComment $fieldsComment

        $repoPath = Join-Path $destRepoDir ("$className" + 'Repository.java')
        $dtoPath = Join-Path $destDtoDir ("$className" + 'DTO.java')
        $servicePath = Join-Path $destServiceDir ("$className" + 'Service.java')
        $serviceImplPath = Join-Path $destServiceImplDir ("$className" + 'ServiceImpl.java')
        $controllerPath = Join-Path $destControllerDir ("$className" + 'Controller.java')
        $modelPath = Join-Path $destModelDir ("$className" + '.java')

        $repoStatus = Safe-WriteFile -Path $repoPath -Content $repoContent -ForceOverwrite
        $dtoStatus = Safe-WriteFile -Path $dtoPath -Content $dtoContent -ForceOverwrite
        $serviceStatus = Safe-WriteFile -Path $servicePath -Content $serviceContent -ForceOverwrite
        $serviceImplStatus = Safe-WriteFile -Path $serviceImplPath -Content $serviceImplContent -ForceOverwrite
        $controllerStatus = Safe-WriteFile -Path $controllerPath -Content $controllerContent -ForceOverwrite
        $modelStatus = Safe-WriteFile -Path $modelPath -Content $modelContent -ForceOverwrite

        $moduleReport.Entities += [ordered]@{
            Entity = $className; Repo = $repoStatus; Service = $serviceStatus; ServiceImpl = $serviceImplStatus; Controller = $controllerStatus; DTO = $dtoStatus; Model = $modelStatus; Package = $basePackage; Paths = @{ Repo=$repoPath; Service=$servicePath; Controller=$controllerPath; Model=$modelPath }
        }

        # Añadir fila global para reporte CSV
        $row = @{ Module = Split-Path $moduleRoot -Leaf; Entity = $className; Repo = $repoStatus; Service = $serviceStatus; Controller = $controllerStatus }
        $globalRows += $row
    }

    # Manejar pom.xml: crear o actualizar propiedades de encoding y plugin Spring Boot
    $pomPath = Join-Path $moduleRoot 'pom.xml'
    if (-not (Test-Path $pomPath)) {
        $artifactId = Split-Path $moduleRoot -Leaf
        $groupId = 'com.inclusive' # predeterminado; puedes personalizar
        $pomContent = Template-Pom -artifactId $artifactId -groupId $groupId
        Safe-WriteFile -Path $pomPath -Content $pomContent -ForceOverwrite | Out-Null
        Write-Host "[INFO] pom.xml creado en: $moduleRoot" -ForegroundColor Green
    } else {
        # Insertar propiedades si faltan y plugin si falta (simple parsing)
        $pomText = Get-Content $pomPath -Raw -ErrorAction SilentlyContinue
        $changed = $false
        if ($pomText -notmatch '<project.build.sourceEncoding>') {
            $pomText = $pomText -replace '(?s)(<properties>)(.*?)(</properties>)', "`$1`r`n    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`r`n    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>`r`n`$2`r`n`$3"
            $changed = $true
        }
        if ($pomText -notmatch 'spring-boot-maven-plugin') {
            if ($pomText -match '(?s)</plugins>') {
                $pomText = $pomText -replace '(?s)(</plugins>)', "    <plugin>`r`n      <groupId>org.springframework.boot</groupId>`r`n      <artifactId>spring-boot-maven-plugin</artifactId>`r`n      <configuration>`r`n        <executable>true</executable>`r`n        <jvmArguments>-Dfile.encoding=UTF8</jvmArguments>`r`n      </configuration>`r`n    </plugin>`r`n`$1"
                $changed = $true
            } else {
                # agregar sección build/plugins si no existe (caso extremo)
                $insert = "`r`n  <build>`r`n    <plugins>`r`n      <plugin>`r`n        <groupId>org.springframework.boot</groupId>`r`n        <artifactId>spring-boot-maven-plugin</artifactId>`r`n        <configuration>`r`n          <executable>true</executable>`r`n          <jvmArguments>-Dfile.encoding=UTF8</jvmArguments>`r`n        </configuration>`r`n      </plugin>`r`n    </plugins>`r`n  </build>`r`n"
                $pomText = $pomText + $insert
                $changed = $true
            }
        }
        if ($changed) { Safe-WriteFile -Path $pomPath -Content $pomText -ForceOverwrite | Out-Null; Write-Host "[INFO] pom.xml actualizado en: $moduleRoot" -ForegroundColor Green }
    }

    # Generar README.md para el módulo con resumen de Entidades (SOBRESCRIBE)
    $readmePath = Join-Path $moduleRoot 'README.md'
    $now = (Get-Date).ToString('yyyy-MM-dd HH:mm:ss')
    $md = "# README - Módulo: $($moduleReport.Module)`r`n`r`n"
    $md += "**Ruta del módulo:** $($moduleReport.ModulePath)`r`n`r`n"
    $md += "**Generado el:** $now`r`n`r`n"
    $md += "## Entidades generadas`r`n`r`n"
    $md += "| Entidad | Controller | Service | Repo | DTO | Model |`r`n|---|---:|---:|---:|---:|---:|`r`n"
    foreach ($e in $moduleReport.Entities) {
        $md += "| $($e.Entity) | $($e.Controller) | $($e.Service) | $($e.Repo) | $($e.DTO) | $($e.Model) |`r`n"
    }
    $md += "`r`n## Endpoints`r`n`r`n"

    foreach ($e in $moduleReport.Entities) {
        $entityLower = $e.Entity.ToLower()
        $md += "### $($e.Entity)`r`n"
        $md += "- GET /api/$entityLower/list`r`n"
        $md += "- GET /api/$entityLower/{id}`r`n"
        $md += "- POST /api/$entityLower`r`n"
        $md += "- PUT /api/$entityLower/{id}`r`n"
        $md += "- DELETE /api/$entityLower/{id}`r`n`r`n"
    }
    $md += "`r`n_Generado por repair-crud-modules-v11.1.ps1_`r`n"
    # Sobrescribir README.md (comportamiento confirmado por el usuario)
    Write-Utf8NoBom -Path $readmePath -Content $md

    $moduleSummaries += $moduleReport
}

# ------------------------------------------------------------
# Generar reporte global CSV y HTML a partir de $globalRows
# ------------------------------------------------------------
# CSV header
$csvHeader = 'Module,Entity,Repo,Service,Controller'
$csvLines = @($csvHeader)
foreach ($r in $globalRows) {
    $csvLines += "$($r.Module),$($r.Entity),$($r.Repo),$($r.Service),$($r.Controller)"
}
Write-Utf8NoBom -Path $csvReport -Content ($csvLines -join "`r`n")

# HTML simple
$html = @"
<!doctype html>
<html>
<head>
<meta charset=\"utf-8\">
<title>Report Endpoints</title>
<style>table{border-collapse:collapse}td,th{border:1px solid #ccc;padding:6px}</style>
</head>
<body>
<h1>Report - Endpoints generados</h1>
<table>
<tr><th>Module</th><th>Entity</th><th>Repo</th><th>Service</th><th>Controller</th></tr>
"@
foreach ($r in $globalRows) {
    $html += "<tr><td>$(Csv-Escape $r.Module)</td><td>$(Csv-Escape $r.Entity)</td><td>$(Csv-Escape $r.Repo)</td><td>$(Csv-Escape $r.Service)</td><td>$(Csv-Escape $r.Controller)</td></tr>`r`n"
}
$html += @"
</table>
</body>
</html>
"@
Write-Utf8NoBom -Path $htmlReport -Content $html

# ------------------------------------------------------------
# Generar README global en la raíz con índice de módulos
# ------------------------------------------------------------
$rootReadme = Join-Path $BasePath 'README_PROJECT.md'
$rootMd = "# Índice de Módulos y CRUDs generados`r`n`r`n"
$rootMd += "Generado por repair-crud-modules-v11.1.ps1`r`n`r`n"
$rootMd += "| Módulo | Entidades procesadas | README |`r`n|---|---:|---|`r`n"
foreach ($m in $moduleSummaries) {
    $modName = $m.Module
    $count = $m.Entities.Count
    # Construir enlace relativo seguro
    $relPath = [IO.Path]::GetRelativePath($BasePath, $m.ModulePath)
    $readmeLink = './' + ($relPath -replace '\\','/') + '/README.md'
    $rootMd += "| $modName | $count | [README]($readmeLink) |`r`n"
}
Write-Utf8NoBom -Path $rootReadme -Content $rootMd

Write-Host "[DONE] Reparación completa. Reportes en: $ReportsDir" -ForegroundColor Green

# Fin del script
