# ILP - Backend Auto-Fix Script 2.0
# Recorre el backend, corrige configuración de microservicios y aplica fix a la BD (students).

# ==== CONFIGURACIÓN DB (AJUSTAR A TU ENTORNO) ====
$DbFixEnabled     = $true          # Pon $false si no quieres tocar la BD
$PostgresHost     = "localhost"
$PostgresPort     = 5432
$PostgresDb       = "inclusive_learning"   # TODO: cambiar por el nombre real
$PostgresUser     = "postgres"
$PostgresPassword = "postgres"
$PsqlPath         = "psql"         # Asume que 'psql' está en el PATH

# ==== CONFIGURACIÓN DE PUERTOS POR SERVICIO (ESTABLES) ====
$PreferredPorts = @{
    "gateway-service"             = 8080
    "auth-service"                = 8081
    "commons-service"             = 8082
    "common-libs"                 = 8083
    "adaptive-education-service"  = 8084
    "assessment-service"          = 8085
    "monitoring-service"          = 8086
    "report-service"              = 8087
    "notification-service"        = 8088
    "tenant-service"              = 8089
}

Write-Host "==============================================="
Write-Host " ILP Backend Auto-Fix Script 2.0"
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

    if ($pom -notmatch "</dependencies>") {
        Write-Host "   WARNING: <dependencies> not found in pom.xml. Skipping Actuator."
        return
    }

    Write-Host "   Adding Actuator dependency..."

    $dependency = @"
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
"@

    $newPom = $pom -replace "</dependencies>", "$dependency`n</dependencies>"
    Set-Content $pomPath $newPom -Encoding UTF8
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
# -------------------------------------------------------
function Assign-Port {
    param(
        [string]$serviceName,
        [string]$servicePath,
        [hashtable]$preferredPorts,
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

    if ($preferredPorts.ContainsKey($serviceName)) {
        $port = $preferredPorts[$serviceName]
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
# Function: Run DB Auto-Fix for students table (mode 2)
# -------------------------------------------------------
function Run-DbAutoFix {
    param(
        [string]$psqlPath,
        [string]$host,
        [int]$port,
        [string]$db,
        [string]$user,
        [string]$password
    )

    Write-Host ""
    Write-Host "Running DB auto-fix for students (first_name/last_name NULL)..."

    $env:PGPASSWORD = $password

    $sql = @"
UPDATE students
SET first_name = COALESCE(first_name, 'Demo'),
    last_name  = COALESCE(last_name,  'Student')
WHERE first_name IS NULL OR last_name IS NULL;
"@

    try {
        & $psqlPath -h $host -p $port -U $user -d $db -c $sql
        Write-Host "   DB fix executed successfully."
    } catch {
        Write-Host "   ERROR running DB fix. Please check psql path and connection parameters."
        Write-Host "   Exception: $($_.Exception.Message)"
    } finally {
        $env:PGPASSWORD = ""
    }
}

# -------------------------------------------------------
# Function: Run CRUD generator if present
# -------------------------------------------------------
function Run-CrudGenerator {
    param(
        [string]$rootPath
    )

    $generator = Join-Path $rootPath "ilp_generate_crud_e2e_tests.ps1"
    if (Test-Path $generator) {
        Write-Host ""
        Write-Host "Running ilp_generate_crud_e2e_tests.ps1..."
        & $generator
    } else {
        Write-Host ""
        Write-Host "WARNING: ilp_generate_crud_e2e_tests.ps1 not found. Skipping CRUD regeneration."
    }
}

# -------------------------------------------------------
# Identify web microservices (with spring-boot-starter-web)
# -------------------------------------------------------
Write-Host ""
Write-Host "Identifying Spring Boot web microservices..."
Write-Host "-------------------------------------------"

$webServices = @()

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
        Write-Host " - $($svc.Name) is not a web service. Skipping config."
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

$nextPort = 8100  # fallback incremental ports if not in PreferredPorts

foreach ($svc in $webServices) {
    Write-Host ""
    Write-Host "Service: $($svc.Name)"

    Add-ActuatorDependency -pomPath $svc.Pom
    Setup-ActuatorConfig  -servicePath $svc.Path
    Assign-Port           -serviceName $svc.Name -servicePath $svc.Path -preferredPorts $PreferredPorts -nextPortRef ([ref]$nextPort)
}

# -------------------------------------------------------
# Show final port mapping
# -------------------------------------------------------
Write-Host ""
Write-Host "Reading assigned ports for web microservices..."
Write-Host "----------------------------------------------"

$serviceInfo = @()
foreach ($svc in $webServices) {
    $port = Get-ServicePort -servicePath $svc.Path
    if ($port) {
        Write-Host " - $($svc.Name): port $port"
    } else {
        Write-Host " - WARNING: No port detected for $($svc.Name)."
    }
    $serviceInfo += [pscustomobject]@{
        Name = $svc.Name
        Path = $svc.Path
        Port = $port
    }
}

# -------------------------------------------------------
# Run DB Auto-fix (if enabled)
# -------------------------------------------------------
if ($DbFixEnabled) {
    Run-DbAutoFix -psqlPath $PsqlPath -host $PostgresHost -port $PostgresPort -db $PostgresDb -user $PostgresUser -password $PostgresPassword
} else {
    Write-Host ""
    Write-Host "DB fix is disabled (DbFixEnabled = \$false). Skipping DB changes."
}

# -------------------------------------------------------
# Run CRUD generator (if present)
# -------------------------------------------------------
Run-CrudGenerator -rootPath $root

Write-Host ""
Write-Host "==============================================="
Write-Host " Auto-Fix completed. Backend ready to start."
Write-Host "==============================================="
Write-Host "Ahora puedes arrancar cada microservicio con:"
Write-Host "   mvn spring-boot:run"
Write-Host "y luego ejecutar las pruebas E2E con:"
Write-Host "   mvn --% -pl e2e-tests -Dilp.e2e.base-url=http://localhost:8080 test"
Write-Host ""
