# ==================================================================
# Archivo: repair-crud-modules-v10.ps1
# Ubicación: inclusive-learning-platform-backend/
# Propósito: Generar y actualizar automáticamente CRUD + Swagger
#           para todas las entidades Java en un proyecto Spring Boot.
#           Genera reportes CSV y HTML en reports/
# Compatible: PowerShell 5.1 y 7+
# ==================================================================

# Función segura para escribir UTF8 sin BOM
function Write-Utf8NoBomSafe {
    param(
        [Parameter(Mandatory)]
        [string]$Path,
        [Parameter(Mandatory)]
        [string]$Content
    )
    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($Path, $Content, $utf8NoBom)
}

# Función para detectar todas las entidades Java
function Get-JavaEntities {
    param(
        [string]$BasePath
    )
    $entities = @()
    # 🔍 Buscar todos los archivos Java en todos los submódulos
$BasePath = "C:\Users\iesaf\OneDrive\Documentos\TEAC2025-26\Reconstruccion_19102025\inclusive-learning-platform-backend"
Write-Host "[INFO] Escaneando módulos Java recursivamente desde: $BasePath" -ForegroundColor Cyan

$javaFiles = Get-ChildItem -Path $BasePath -Recurse -Filter *.java -ErrorAction SilentlyContinue

if ($javaFiles.Count -eq 0) {
    Write-Host "[ERROR] No se encontraron archivos .java en la ruta $BasePath" -ForegroundColor Red
    exit
}

Write-Host "[INFO] Se encontraron $($javaFiles.Count) archivos Java." -ForegroundColor Green


    foreach ($file in $javaFiles) {
        if (-not $file) { continue }
if (-not (Test-Path $file)) {
    Write-Host "[WARN] Archivo no encontrado: $file" -ForegroundColor Yellow
    continue
}

        $content = Get-Content $file -Raw
        if ($content -match '@Entity') {
            $entities += [PSCustomObject]@{
                Path = $file.FullName
                ClassName = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
                Package = if ($content -match 'package\s+([\w\.]+);') { $matches[1] } else { "com.example" }
            }
        }
    }
    return $entities
}

# Función para generar CRUD por entidad
function Generate-CRUD {
    param(
        [PSCustomObject]$Entity,
        [string]$BasePath
    )

    $packagePath = $Entity.Package -replace '\.', '/'
    $controllerDir = Join-Path $BasePath "src/main/java/$packagePath/controller"
    $serviceDir    = Join-Path $BasePath "src/main/java/$packagePath/service"
    $repoDir       = Join-Path $BasePath "src/main/java/$packagePath/repository"

    foreach ($dir in @($controllerDir, $serviceDir, $repoDir)) {
        if (-not (Test-Path $dir)) { New-Item -Path $dir -ItemType Directory -Force | Out-Null }
    }

    $entityName = $Entity.ClassName
    $lcName = ($entityName.Substring(0,1).ToLower() + $entityName.Substring(1))

    # Repository
    $repoFile = Join-Path $repoDir "${entityName}Repository.java"
    $repoContent = @"
package $($Entity.Package).repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import $($Entity.Package).$entityName;

@Repository
public interface ${entityName}Repository extends JpaRepository<$entityName, Long> {}
"@
    Write-Utf8NoBomSafe -Path $repoFile -Content $repoContent

    # Service
    $serviceFile = Join-Path $serviceDir "${entityName}Service.java"
    $serviceContent = @"
package $($Entity.Package).service;

import org.springframework.stereotype.Service;
import $($Entity.Package).$entityName;
import $($Entity.Package).repository.${entityName}Repository;
import java.util.List;
import java.util.Optional;

@Service
public class ${entityName}Service {
    private final ${entityName}Repository repository;

    public ${entityName}Service(${entityName}Repository repository) {
        this.repository = repository;
    }

    public List<$entityName> findAll() { return repository.findAll(); }
    public Optional<$entityName> findById(Long id) { return repository.findById(id); }
    public $entityName save($entityName obj) { return repository.save(obj); }
    public void deleteById(Long id) { repository.deleteById(id); }
}
"@
    Write-Utf8NoBomSafe -Path $serviceFile -Content $serviceContent

    # Controller
    $controllerFile = Join-Path $controllerDir "${entityName}Controller.java"
    $controllerContent = @"
package $($Entity.Package).controller;

import org.springframework.web.bind.annotation.*;
import $($Entity.Package).$entityName;
import $($Entity.Package).service.${entityName}Service;
import java.util.List;

@RestController
@RequestMapping(""/api/$lcName"")
public class ${entityName}Controller {
    private final ${entityName}Service service;

    public ${entityName}Controller(${entityName}Service service) { this.service = service; }

    @GetMapping
    public List<$entityName> getAll() { return service.findAll(); }

    @GetMapping(""/{id}"")
    public $entityName getById(@PathVariable Long id) { return service.findById(id).orElse(null); }

    @PostMapping
    public $entityName create(@RequestBody $entityName obj) { return service.save(obj); }

    @PutMapping(""/{id}"")
    public $entityName update(@PathVariable Long id, @RequestBody $entityName obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping(""/{id}"")
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}
"@
    Write-Utf8NoBomSafe -Path $controllerFile -Content $controllerContent

    return [PSCustomObject]@{
        Entity = $entityName
        Endpoints = @(
            "/api/$lcName (GET, POST, PUT, DELETE)"
        )
    }
}

# Función para generar reportes CSV y HTML
function Generate-Reports {
    param(
        [array]$Endpoints,
        [string]$ReportDir
    )
    if (-not (Test-Path $ReportDir)) { New-Item -Path $ReportDir -ItemType Directory -Force | Out-Null }

    $csvFile = Join-Path $ReportDir "report_endpoints.csv"
    $htmlFile = Join-Path $ReportDir "report_endpoints.html"

    $Endpoints | Export-Csv -Path $csvFile -NoTypeInformation -Encoding UTF8

    $html = @"
<html>
<head><title>Endpoints Report</title></head>
<body>
<h1>Spring Boot Endpoints Report</h1>
<table border='1'><tr><th>Entity</th><th>Endpoints</th></tr>
"@
    foreach ($ep in $Endpoints) {
        $html += "<tr><td>$($ep.Entity)</td><td>$($ep.Endpoints -join '<br>')</td></tr>`n"
    }
    $html += "</table></body></html>"
    Write-Utf8NoBomSafe -Path $htmlFile -Content $html

    return [PSCustomObject]@{
        CSV = $csvFile
        HTML = $htmlFile
    }
}

# ===================== Script principal =====================
Write-Host "=== Reparación automática avanzada de CRUD + Swagger (v10) ===`n"

$BasePath = Get-Location
$entities = Get-JavaEntities -BasePath $BasePath
$allEndpoints = @()

foreach ($ent in $entities) {
    Write-Host "[INFO] Generando CRUD para entidad: $($ent.ClassName)"
    $ep = Generate-CRUD -Entity $ent -BasePath $BasePath
    $allEndpoints += $ep
}

$reports = Generate-Reports -Endpoints $allEndpoints -ReportDir (Join-Path $BasePath "reports")

Write-Host "[INFO] Reportes generados: $($reports.CSV), $($reports.HTML)"
Write-Host "=== Reparación finalizada ==="
