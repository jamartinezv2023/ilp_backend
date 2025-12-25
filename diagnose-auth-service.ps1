Write-Host "================ AUTH-SERVICE DIAGNOSTIC ================" -ForegroundColor Cyan

Write-Host "`n[1] Java version"
java -version

Write-Host "`n[2] Gradle version"
.\gradlew --version

Write-Host "`n[3] Dependencies (JWT related)"
.\gradlew :auth-service:dependencies --configuration compileClasspath | Select-String "oauth2|jwt|security"

Write-Host "`n[4] Checking Jwt imports"
Get-ChildItem auth-service\src\main\java -Recurse |
    Select-String "oauth2.jwt.Jwt|JwtAuthenticationToken" |
    ForEach-Object { $_.Path }

Write-Host "`n[5] Compile auth-service"
.\gradlew :auth-service:compileJava --stacktrace

Write-Host "`n================ END DIAGNOSTIC ========================="

