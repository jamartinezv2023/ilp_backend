Write-Host "==============================================="
Write-Host " ILP DTO FIX - FORCE RELOAD COMMONS-SERVICE"
Write-Host "==============================================="

# 1. Remove old commons-service from local Maven repo
$m2Path = "$env:USERPROFILE\.m2\repository\com\inclusive\platform\commons-service"

if (Test-Path $m2Path) {
    Write-Host "[INFO] Removing old commons-service from .m2..."
    Remove-Item -Recurse -Force $m2Path
} else {
    Write-Host "[INFO] No previous commons-service found in .m2."
}

# 2. Clean previous builds
Write-Host "[INFO] Cleaning previous builds..."
mvn clean -q

# 3. Rebuild commons-service module
Write-Host "[INFO] Rebuilding commons-service..."
mvn clean install -pl commons-service -am

# 4. Check for StudentDTO inside the JAR
Write-Host "[INFO] Checking StudentDTO in JAR..."
jar tf "commons-service/target/commons-service-1.0.0-SNAPSHOT.jar" | Select-String "StudentDTO"

# 5. Rebuild adaptive-education-service
Write-Host "[INFO] Rebuilding adaptive-education-service..."
mvn clean package -pl adaptive-education-service -am

Write-Host "==============================================="
Write-Host " FIX COMPLETED - StudentDTO should now resolve."
Write-Host "==============================================="
