$root = Get-Location
$out = "radiografia_total_$(Get-Date -Format yyyyMMdd_HHmm).md"

"# RADIOGRAFÍA TOTAL ILP BACKEND" | Out-File $out

"## JAVA" | Out-File $out -Append
java -version 2>&1 | Out-File $out -Append

"## GRADLE MODULES" | Out-File $out -Append
Get-ChildItem -Directory | ForEach-Object {
    if (Test-Path "$($_.Name)/build.gradle.kts") {
        "- $($_.Name)" | Out-File $out -Append
    }
}

"## CLASES SPRING SIN DEPENDENCIAS" | Out-File $out -Append
Get-ChildItem -Recurse -Filter *.java |
Select-String "@RestController|@Service|@Entity|@Configuration" |
ForEach-Object {
    $_.Path
} | Sort-Object | Get-Unique | Out-File $out -Append

"## TESTS ROTOS (referencias inválidas)" | Out-File $out -Append
Get-ChildItem -Recurse src\test -Filter *.java |
Select-String "class\s+\w+" |
Out-File $out -Append

Write-Host "Radiografía generada en $out"
