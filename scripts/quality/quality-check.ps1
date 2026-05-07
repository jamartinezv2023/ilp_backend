Write-Host "=== ILP Backend Quality Check ==="

Write-Host "`n[1] Build completo"
.\gradlew.bat clean build

Write-Host "`n[2] Tests"
.\gradlew.bat test

Write-Host "`n[3] Reporte JaCoCo si está disponible"
.\gradlew.bat jacocoTestReport

Write-Host "`n[4] Verificación finalizada"
