# ILP - Backend Setup and Verification Script 2.0 (AutoRun, robust)

Write-Host "==============================================="
Write-Host " ILP Backend Setup and Verification Script 2.0"
Write-Host "==============================================="

$root = Get-Location
$allServices = Get-ChildItem -Directory | Where-Object { Test-Path "$($_.FullName)\pom.xml" }

Write-Host ""
Write-Host "Modules with pom.xml detected:"
foreach ($svc in $allServices) {
    Write-Host " - $($svc.Name)"
}

# -------------------------------------------------------
# Function: ensure src/main/resources exists
# -------------------------------------------------------
function Ensure-ResourcesFolder {
    param(
        [string]$servicePath
    )

    $resPath = Join-Path $servicePath "src\main\resources"
    if (-not (Test-Path $resPath)) {
        New-Item -ItemType Directory -Path $resPath -Force | Out-Null
        Write-Host "   Created resources folder: $resPath"
    }
    return $resPath
}

# -------------------------------------------------------
# Function: Add Actuator dependency to pom.xml
# -------------------------------------------------------
function Add-ActuatorDependency {
    param(
        [string]$pomPath
    )

    $pom = Get-Content $pomPath -Raw

    if ($pom -match "spring-boot-starter-actuator") {
        Write-Host "   Actuator already present."
        return
    }

    Write-Host "   Adding Actuator dependency..."

    $dependency = @"
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
"@

    if ($pom -match "</dependencies>") {
        $newPom = $pom -replace "</dependencies>", "$dependency`n</dependencies>"
        Set-Content $pomPath $newPom -Encoding UTF8
    } else {
        Write-Host "   WARNING: <dependencies> section not found in pom.xml. Skipping Actuator."
    }
}

# -------------------------------------------------------
# Function: Configure Actuator in application.yml/properties
# -------------------------------------------------------
function Setup-ActuatorConfig {
    param(
        [string]$servicePath
    )

    $resPath = Ensure-ResourcesFolder -servicePath $servicePath
    $yaml = Join-Path $resPath "application.yml"
    $props = Join-Path $resPath "application.properties"

    $yamlBlock = @"
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: always
"@

    $propsBlock = @"
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=always
"@

    if (Test-Path $yaml) {
        $data = Get-Content $yaml -Raw
        if ($data -notmatch "management:") {
            Write-Host "   Adding actuator block to application.yml"
            Add-Content $yaml "`n$yamlBlock"
        } else {
            Write-Host "   Actuator already configured in application.yml."
        }
        return
    }

    if (Test-Path $props) {
        $data = Get-Content $props -Raw
        if ($data -notmatch "management\.endpoints\.web\.exposure\.include") {
            Write-Host "   Adding actuator block to application.properties"
            Add-Content $props "`n$propsBlock"
        } else {
            Write-Host "   Actuator already configured in application.properties."
        }
        return
    }

    Write-Host "   Creating application.yml with actuator config."
    Set-Content $yaml $yamlBlock -Encoding UTF8
}

# -------------------------------------------------------
# Function: Assign port to service if none exists
# gateway-service -> 8080, others -> nextPort (by ref)
# -------------------------------------------------------
function Assign-Port {
    param(
        [string]$serviceName,
        [string]$servicePath,
        [ref]$nextPortRef
    )

    $resPath = Ensure-ResourcesFolder -servicePath $servicePath
    $yaml = Join-Path $resPath "application.yml"
    $props = Join-Path $resPath "application.properties"

    $existingPort = $null

    if (Test-Path $yaml) {
        $data = Get-Content $yaml -Raw
        if ($data -match "server:\s*[\r\n]+\s*port:\s*(\d+)") {
            $existingPort = $matches[1]
        }
    }

    if (-not $existingPort -and (Test-Path $props)) {
        $data = Get-Content $props -Raw
        if ($data -match "server\.port=(\d+)") {
            $existingPort = $matches[1]
        }
    }

    if ($existingPort) {
        Write-Host "   Existing port detected: $existingPort"
        return
    }

    # No port defined yet, assign one
    if ($serviceName -eq "gateway-service") {
        $port = 8080
    } else {
        $port = $nextPortRef.Value
        $nextPortRef.Value = $nextPortRef.Value + 1
    }

    if (Test-Path $yaml) {
        Write-Host "   Assigning port $port in application.yml"
        Add-Content $yaml "`nserver:`n  port: $port"
        return
    }

    if (Test-Path $props) {
        Write-Host "   Assigning port $port in application.properties"
        Add-Content $props "`nserver.port=$port"
        return
    }

    # If neither config existed (very unlikely here, because Setup-ActuatorConfig should have created yaml)
    Write-Host "   Creating application.yml with port $port"
    Set-Content $yaml "server:`n  port: $port" -Encoding UTF8
}

# -------------------------------------------------------
# Function: Read server.port from config
# -------------------------------------------------------
function Get-ServicePort {
    param(
        [string]$servicePath
    )

    $resPath = Join-Path $servicePath "src\main\resources"
    $yaml = Join-Path $resPath "application.yml"
    $props = Join-Path $resPath "application.properties"

    if (Test-Path $yaml) {
        $data = Get-Content $yaml -Raw
        if ($data -match "server:\s*[\r\n]+\s*port:\s*(\d+)") {
            return [int]$matches[1]
        }
    }

    if (Test-Path $props) {
        $data = Get-Content $props -Raw
        if ($data -match "server\.port=(\d+)") {
            return [int]$matches[1]
        }
    }

    return $null
}

# -------------------------------------------------------
# Function: test /actuator/health with retries
# -------------------------------------------------------
function Wait-ForHealth {
    param(
        [string]$serviceName,
        [int]$port,
        [int]$maxSeconds = 60,
        [int]$intervalSeconds = 5
    )

    $url = "http://localhost:$port/actuator/health"
    $elapsed = 0

    Write-Host "   Waiting for $serviceName to be UP at $url ..."

    while ($elapsed -lt $maxSeconds) {
        try {
            $resp = Invoke-WebRequest -Uri $url -TimeoutSec 3 -ErrorAction Stop
            Write-Host "   UP after $elapsed seconds. Response: $($resp.Content)"
            return $true
        } catch {
            Start-Sleep -Seconds $intervalSeconds
            $elapsed += $intervalSeconds
        }
    }

    Write-Host "   STILL DOWN after $maxSeconds seconds."
    return $false
}

# -------------------------------------------------------
# Identify "web" microservices (with spring-boot-starter-web)
# -------------------------------------------------------
$webServices = @()

Write-Host ""
Write-Host "Identifying Spring Boot web microservices..."
Write-Host "-------------------------------------------"

foreach ($svc in $allServices) {
    $pomPath = Join-Path $svc.FullName "pom.xml"
    $pom = Get-Content $pomPath -Raw

    if ($pom -match "spring-boot-starter-web") {
        Write-Host " - $($svc.Name) is a web microservice."
        $webServices += [pscustomobject]@{
            Name = $svc.Name
            Path = $svc.FullName
            Pom  = $pomPath
        }
    } else {
        Write-Host " - $($svc.Name) is NOT a web service. Skipping config/start."
    }
}

if ($webServices.Count -eq 0) {
    Write-Host ""
    Write-Host "No web microservices found. Nothing to configure."
    exit 0
}

# -------------------------------------------------------
# Configure each web microservice: Actuator + port
# -------------------------------------------------------
Write-Host ""
Write-Host "Configuring web microservices (Actuator + ports)..."
Write-Host "---------------------------------------------------"

$nextPort = 8081  # Gateway will be 8080 if needed

foreach ($svc in $webServices) {
    Write-Host ""
    Write-Host "Service: $($svc.Name)"

    Add-ActuatorDependency -pomPath $svc.Pom
    Setup-ActuatorConfig -servicePath $svc.Path
    Assign-Port -serviceName $svc.Name -servicePath $svc.Path -nextPortRef ([ref]$nextPort)
}

# -------------------------------------------------------
# Regenerate CRUD E2E tests (if script exists)
# -------------------------------------------------------
Write-Host ""
Write-Host "Regenerating CRUD E2E tests (if script exists)..."
Write-Host "------------------------------------------------"

$generator = Join-Path $root "ilp_generate_crud_e2e_tests.ps1"
if (Test-Path $generator) {
    Write-Host "Running ilp_generate_crud_e2e_tests.ps1..."
    & $generator
} else {
    Write-Host "WARNING: ilp_generate_crud_e2e_tests.ps1 not found. Skipping CRUD regeneration."
}

# -------------------------------------------------------
# Read ports and prepare info objects
# -------------------------------------------------------
$serviceInfo = @()

Write-Host ""
Write-Host "Reading assigned ports for web microservices..."
Write-Host "----------------------------------------------"

foreach ($svc in $webServices) {
    $port = Get-ServicePort -servicePath $svc.Path
    if (-not $port) {
        Write-Host " - WARNING: No port detected for $($svc.Name)."
    } else {
        Write-Host " - $($svc.Name): port $port"
    }

    $serviceInfo += [pscustomobject]@{
        Name = $svc.Name
        Path = $svc.Path
        Port = $port
        Status = "UNKNOWN"
    }
}

# -------------------------------------------------------
# Start services SEQUENTIALLY and wait for UP
# -------------------------------------------------------
Write-Host ""
Write-Host "Starting web microservices sequentially..."
Write-Host "------------------------------------------"

foreach ($info in $serviceInfo) {

    if (-not $info.Port -or $info.Port -eq 0) {
        Write-Host ""
        Write-Host "Skipping start of $($info.Name) because port is invalid."
        $info.Status = "SKIPPED_NO_PORT"
        continue
    }

    Write-Host ""
    Write-Host "Starting $($info.Name) on port $($info.Port)..."

    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd `"$($info.Path)`"; mvn spring-boot:run" | Out-Null

    # Espera a que el servicio se ponga UP
    $up = Wait-ForHealth -serviceName $info.Name -port $info.Port -maxSeconds 90 -intervalSeconds 5
    if ($up) {
        $info.Status = "UP"
    } else {
        $info.Status = "DOWN"
    }
}

# -------------------------------------------------------
# Summary
# -------------------------------------------------------
Write-Host ""
Write-Host "================================================"
Write-Host " Backend status summary"
Write-Host "================================================"

foreach ($info in $serviceInfo) {
    Write-Host (" - {0} : port {1} -> {2}" -f $info.Name, $info.Port, $info.Status)
}

Write-Host ""
Write-Host "================================================"
Write-Host " Backend is ready (if services are UP) for E2E tests."
Write-Host "================================================"
Write-Host "Para lanzar las pruebas E2E desde PowerShell, usa:"
Write-Host ""
Write-Host "  mvn --% -pl e2e-tests -Dilp.e2e.base-url=http://localhost:8080 test"
Write-Host ""
