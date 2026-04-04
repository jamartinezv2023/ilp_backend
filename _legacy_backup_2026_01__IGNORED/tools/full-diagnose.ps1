# ===========================
# ILP BACKEND – FULL DIAGNOSTIC
# ===========================

Write-Host '=== ENV ==='
java -version
./gradlew --version

Write-Host '=== GRADLE MODULES ==='
Get-Content settings.gradle.kts

Write-Host '=== JAVA SOURCES COUNT ==='
Get-ChildItem -Recurse -Filter *.java | Measure-Object

Write-Host '=== DUPLICATED CONTROLLERS ==='
Get-ChildItem src -Recurse -Filter *.java |
 Select-String '@RestController' |
 Group-Object Line |
 Where-Object { .Count -gt 1 } |
 ForEach-Object {
   Write-Host 'DUPLICATE:'
   .Group | ForEach-Object { Write-Host .Path }
 }

Write-Host '=== SECURITY CONFIGS ==='
Get-ChildItem -Recurse -Filter *.java |
 Select-String 'SecurityFilterChain|@EnableWebSecurity|@Configuration' |
 Select Path, LineNumber, Line

Write-Host '=== TEST TYPES ==='
Get-ChildItem -Recurse -Filter '*Test.java' |
 Group-Object {
   if (.FullName -match 'e2e') {'E2E'}
   elseif (.FullName -match 'integration') {'INTEGRATION'}
   else {'UNIT'}
 }

Write-Host '=== SPRING BOOT APPS ==='
Get-ChildItem -Recurse -Filter '*Application.java'

Write-Host '=== UNUSED TEST CONTROLLERS ==='
Get-ChildItem src/test -Recurse -Filter *.java |
 Select-String '@RestController'

Write-Host '=== BUILD STATUS ==='
./gradlew clean build -x test

