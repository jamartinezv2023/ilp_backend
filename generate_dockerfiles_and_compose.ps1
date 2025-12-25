# ==============================================
# Script: generate_dockerfiles_and_compose.ps1
# Ubicación: C:\TEAC2026\ilp_backend\
# Función: Genera Dockerfile para cada servicio y docker-compose.dev.yml limpio
# ==============================================

# Ruta base del proyecto
$basePath = "C:\TEAC2026\ilp_backend"

# Lista de servicios a generar
$services = @(
    "auth-service",
    "tenant-service",
    "assessment-service",
    "adaptive-education-service",
    "notification-service",
    "gateway-service",
    "commons-service",
    "report-service",
    "monitoring-service"
)

# ==============================================
# 1. Generar Dockerfile para cada servicio
# ==============================================
foreach ($service in $services) {
    $dockerfilePath = Join-Path "$basePath\$service" "Dockerfile"
    if (!(Test-Path $dockerfilePath)) {
        $dockerfileContent = @"
# Dockerfile para $service
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
"@
        # Guardar Dockerfile con codificación UTF8
        $dockerfileContent | Set-Content -Path $dockerfilePath -Encoding UTF8
        Write-Host "Dockerfile generado para $service"
    } else {
        Write-Host "Dockerfile ya existe para $service, se omite."
    }
}

# ==============================================
# 2. Generar docker-compose.dev.yml limpio
# ==============================================
$composeFilePath = Join-Path $basePath "docker-compose.dev.yml"

$servicesYAML = @()
$servicesYAML += "version: '3.9'"
$servicesYAML += ""
$servicesYAML += "services:"
$servicesYAML += "  postgres-dev:"
$servicesYAML += "    image: postgres:15"
$servicesYAML += "    environment:"
$servicesYAML += "      POSTGRES_USER: postgres"
$servicesYAML += "      POSTGRES_PASSWORD: postgres"
$servicesYAML += "    ports:"
$servicesYAML += "      - '5432:5432'"
$servicesYAML += "    volumes:"
$servicesYAML += "      - postgres-data:/var/lib/postgresql/data"

# Servicios dinámicos
$port = 8081
foreach ($service in $services) {
    $servicesYAML += "  ${service}:"
    $servicesYAML += "    build: ./${service}"
    $servicesYAML += "    ports:"
    $servicesYAML += "      - '${port}:8080'"
    $servicesYAML += "    depends_on:"
    $servicesYAML += "      - postgres-dev"
    $port++
}

$servicesYAML += ""
$servicesYAML += "volumes:"
$servicesYAML += "  postgres-data:"

# Guardar docker-compose.dev.yml con codificación UTF8
$servicesYAML | Set-Content -Path $composeFilePath -Encoding UTF8

Write-Host "docker-compose.dev.yml limpio y sin duplicados generado correctamente."
Write-Host "Para levantar todo: docker-compose -f docker-compose.dev.yml up --build"
