$root = Get-Location
$report = "$root\radiografia_total.txt"

"=== RADIOGRAFÍA TOTAL DEL PROYECTO ===" | Out-File $report

"== MÓDULOS GRADLE ==" | Out-File $report -Append
Get-ChildItem -Directory | Where-Object {
    Test-Path "$($_.FullName)\build.gradle.kts"
} | ForEach-Object {
    $_.Name | Out-File $report -Append
}

"== CLASES SPRING SIN DEPENDENCIAS ==" | Out-File $report -Append
Get-ChildItem -Recurse -Filter *.java |
Select-String "@SpringBootApplication|@RestController|@Entity|@Service" |
ForEach-Object {
    $_.Path
} | Sort-Object -Unique | Out-File $report -Append

"== TESTS EXISTENTES ==" | Out-File $report -Append
Get-ChildItem -Recurse src\test | Where-Object { $_.Extension -match "java|kt|feature" } |
Select-Object FullName | Out-File $report -Append

"== DUPLICADOS SOSPECHOSOS ==" | Out-File $report -Append
Get-ChildItem -Recurse -Filter *.java |
Group-Object Name |
Where-Object { $_.Count -gt 1 } |
ForEach-Object {
    $_.Name
} | Out-File $report -Append

"== DEPENDENCIAS POR MÓDULO ==" | Out-File $report -Append
Get-ChildItem -Recurse -Filter build.gradle.kts |
ForEach-Object {
    "`n--- $($_.FullName) ---" | Out-File $report -Append
    Get-Content $_ | Select-String "implementation|testImplementation" |
    Out-File $report -Append
}
