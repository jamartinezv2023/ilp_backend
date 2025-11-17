# ===============================================
# VERIFICACIÃ“N AVANZADA - adaptive-education-service
# ===============================================

$solutionRoot = "C:\temp_ilp\inclusive-learning-platform-backend"
$moduleRoot   = Join-Path $solutionRoot "adaptive-education-service"
$javaRoot     = Join-Path $moduleRoot "src\main\java\com\inclusive\adaptiveeducationservice"
$entityRoot   = Join-Path $javaRoot "entity"

$global:failCount = 0

function Section {
    param([string]$title)
    Write-Host ""
    Write-Host "========== $title ==========" -ForegroundColor Cyan
}

function Fail {
    param([string]$msg)
    $global:failCount++
    Write-Host "[ERROR] $msg" -ForegroundColor Red
}

function Ok {
    param([string]$msg)
    Write-Host "[OK] $msg" -ForegroundColor Green
}

function Check-Path {
    param([string]$path, [string]$desc)
    if (Test-Path $path) {
        Ok "$desc â†’ existe ($path)"
        return $true
    } else {
        Fail "$desc â†’ NO existe ($path)"
        return $false
    }
}

function Check-FileContains {
    param(
        [string]$file,
        [string]$pattern,
        [string]$desc
    )

    if (!(Test-Path $file)) {
        Fail "No se puede revisar '$desc' porque no existe el archivo: $file"
        return
    }

    $content = Get-Content $file -Raw
    if ($content -match $pattern) {
        Ok "$desc encontrado en $([System.IO.Path]::GetFileName($file))"
    } else {
        Fail "$desc NO encontrado en $([System.IO.Path]::GetFileName($file))"
    }
}

function Check-Package {
    param(
        [string]$file,
        [string]$expectedPackage
    )

    if (!(Test-Path $file)) {
        Fail "No se puede revisar el package porque falta el archivo: $file"
        return
    }

    $lines = Get-Content $file
    $pkgLine = $lines | Where-Object { $_.Trim().StartsWith("package ") } | Select-Object -First 1

    if (-not $pkgLine) {
        Fail "El archivo $file no tiene sentencia 'package'."
        return
    }

    if ($pkgLine.Trim() -eq "package $expectedPackage;") {
        Ok "Package correcto en $([System.IO.Path]::GetFileName($file)) â†’ $expectedPackage"
    } else {
        Fail "Package INCORRECTO en $([System.IO.Path]::GetFileName($file)). Esperado: 'package $expectedPackage;' pero se encontrÃ³: '$pkgLine'"
    }
}

# ---------------------------
# 1. ESTRUCTURA
# ---------------------------

Section "1. Estructura bÃ¡sica de carpetas"

$dirs = @(
    @{ Path = $javaRoot;                      Desc = "RaÃ­z del cÃ³digo Java del mÃ³dulo" },
    @{ Path = (Join-Path $javaRoot "client"); Desc = "Paquete client" },
    @{ Path = (Join-Path $javaRoot "config"); Desc = "Paquete config" },
    @{ Path = (Join-Path $javaRoot "controller"); Desc = "Paquete controller (proxy)" },
    @{ Path = $entityRoot;                       Desc = "Paquete entity" },
    @{ Path = (Join-Path $entityRoot "model");   Desc = "Paquete entity.model" },
    @{ Path = (Join-Path $entityRoot "dto");     Desc = "Paquete entity.dto" },
    @{ Path = (Join-Path $entityRoot "repository"); Desc = "Paquete entity.repository" },
    @{ Path = (Join-Path $entityRoot "service");    Desc = "Paquete entity.service" },
    @{ Path = (Join-Path $entityRoot "service\impl"); Desc = "Paquete entity.service.impl" }
)

foreach ($d in $dirs) {
    Check-Path $d.Path $d.Desc | Out-Null
}

# ---------------------------
# 2. ARCHIVOS
# ---------------------------

Section "2. Archivos claves del mÃ³dulo"

$appFile         = Join-Path $javaRoot "AdaptiveEducationServiceApplication.java"
$studentEntity   = Join-Path $entityRoot "model\Student.java"
$studentDTO      = Join-Path $entityRoot "dto\StudentDTO.java"
$studentRepo     = Join-Path $entityRoot "repository\StudentRepository.java"
$studentService  = Join-Path $entityRoot "service\StudentService.java"
$studentServiceImpl = Join-Path $entityRoot "service\impl\StudentServiceImpl.java"
$studentController  = Join-Path $entityRoot "controller\StudentController.java"

$studentClient   = Join-Path $javaRoot "client\StudentClient.java"
$studentProxyCtrl = Join-Path $javaRoot "controller\StudentProxyController.java"
$dataInitializer = Join-Path $javaRoot "config\DataInitializer.java"
$swaggerConfig   = Join-Path $javaRoot "config\SwaggerConfig.java"
$studentIntegrationService = Join-Path $javaRoot "service\StudentIntegrationService.java"

$files = @(
    $appFile, $studentEntity, $studentDTO, $studentRepo, $studentService,
    $studentServiceImpl, $studentController, $studentClient, $studentProxyCtrl,
    $dataInitializer, $swaggerConfig, $studentIntegrationService
)

foreach ($f in $files) {
    Check-Path $f "$f" | Out-Null
}

# ---------------------------
# 3. PACKAGES
# ---------------------------

Section "3. VerificaciÃ³n de packages"

Check-Package $appFile           "com.inclusive.adaptiveeducationservice"
Check-Package $studentEntity     "com.inclusive.adaptiveeducationservice.entity.model"
Check-Package $studentDTO        "com.inclusive.adaptiveeducationservice.entity.dto"
Check-Package $studentRepo       "com.inclusive.adaptiveeducationservice.entity.repository"
Check-Package $studentService    "com.inclusive.adaptiveeducationservice.entity.service"
Check-Package $studentServiceImpl "com.inclusive.adaptiveeducationservice.entity.service.impl"
Check-Package $studentController "com.inclusive.adaptiveeducationservice.entity.controller"
Check-Package $studentClient     "com.inclusive.adaptiveeducationservice.client"
Check-Package $studentProxyCtrl  "com.inclusive.adaptiveeducationservice.controller"
Check-Package $dataInitializer   "com.inclusive.adaptiveeducationservice.config"
Check-Package $swaggerConfig     "com.inclusive.adaptiveeducationservice.config"
Check-Package $studentIntegrationService "com.inclusive.adaptiveeducationservice.service"

# ---------------------------
# 4. ANOTACIONES
# ---------------------------

Section "4. VerificaciÃ³n de anotaciones y clases"

Check-FileContains $appFile "@SpringBootApplication" "Clase principal con @SpringBootApplication"
Check-FileContains $studentEntity "@Entity" "Entidad Student tiene @Entity"
Check-FileContains $studentRepo "extends JpaRepository" "Repositorio extiende JpaRepository"
Check-FileContains $studentServiceImpl "@Service" "ServiceImpl anotado con @Service"
Check-FileContains $studentController "@RestController" "Controller anotado con @RestController"

# ---------------------------
# 5. BUILD
# ---------------------------

Section "5. CompilaciÃ³n del mÃ³dulo con Maven"

cd $solutionRoot

$mvn = "mvn.cmd"
if (!(Get-Command $mvn -ErrorAction SilentlyContinue)) {
    Fail "Maven no estÃ¡ instalado o no estÃ¡ en el PATH"
} else {
    Write-Host "Ejecutando Maven..." -ForegroundColor Yellow
    & $mvn clean package -pl adaptive-education-service -am -DskipTests
}

# ---------------------------
# 6. RESUMEN FINAL
# ---------------------------

Section "RESULTADO FINAL"

if ($global:failCount -eq 0) {
    Write-Host "âœ… El mÃ³dulo pasÃ³ TODAS las verificaciones." -ForegroundColor Green
} else {
    Write-Host "âŒ Se detectaron $global:failCount errores. Revisa el log." -ForegroundColor Red
}

Write-Host "==============================================="
Write-Host "FIN DE LA VERIFICACION AVANZADA"
Write-Host "==============================================="

