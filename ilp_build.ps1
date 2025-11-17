<#
  ILP BUILD SCRIPT (Maven + Dockerfile check)
  -------------------------------------------
  Uso:
    PS> ./ilp_build.ps1
#>

param(
  [string]$ProjectRoot = $(Split-Path -Parent $PSCommandPath)
)

Write-Host "==============================================="
Write-Host " ILP BUILD SCRIPT — MAVEN + DOCKERFILE"
Write-Host "==============================================="
Write-Host "ProjectRoot: $ProjectRoot" -ForegroundColor Yellow

# -------------------------------
# Verificar Dockerfile
# -------------------------------
$dockerfilePath = Join-Path $ProjectRoot "adaptive-education-service/Dockerfile"

if (-not (Test-Path $dockerfilePath)) {
    $dockerfileContent = @'
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY . .

RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY target/adaptive-education-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
'@
    Set-Content -Path $dockerfilePath -Value $dockerfileContent -Encoding UTF8
    Write-Host "[OK] Dockerfile generado correctamente." -ForegroundColor Green
} else {
    Write-Host "[INFO] Dockerfile ya existente." -ForegroundColor Yellow
}

# -------------------------------
# Compilar Maven
# -------------------------------
Write-Host "[INFO] Ejecutando Maven build..." -ForegroundColor Cyan
Push-Location $ProjectRoot
try {
    mvn clean package -pl adaptive-education-service -am
    Write-Host "[OK] Build completado sin errores." -ForegroundColor Green
}
catch {
    Write-Host "[ERROR] Falló el build Maven." -ForegroundColor Red
    Write-Host $_.Exception.Message
}
finally {
    Pop-Location
}

Write-Host "==============================================="
Write-Host " ILP BUILD SCRIPT COMPLETADO"
Write-Host "==============================================="
