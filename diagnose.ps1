# Location: tools/diagnose.ps1
# Ejecuta: powershell -ExecutionPolicy Bypass -File tools/diagnose.ps1
$ErrorActionPreference = "Stop"

$root = (Get-Location).Path
$diag = Join-Path $root "diagnostics"
$ts = Get-Date -Format "yyyyMMdd_HHmmss"
$out = Join-Path $diag $ts

New-Item -ItemType Directory -Force -Path $out | Out-Null

function Write-Section($name) {
  "`n==================== $name ====================`n" | Tee-Object -FilePath (Join-Path $out "_index.txt") -Append | Out-Null
}

Write-Section "ENV"
@"
Date: $(Get-Date)
Repo: $root
PowerShell: $($PSVersionTable.PSVersion)
Java: $(java -version 2>&1 | Out-String)
Gradle: $(.\gradlew -v 2>&1 | Out-String)
"@ | Out-File (Join-Path $out "env.txt") -Encoding utf8

Write-Section "TREE"
# Estructura (rápida)
Get-ChildItem -Recurse -Force |
  Select-Object FullName, Length, LastWriteTime |
  Export-Csv (Join-Path $out "tree.csv") -NoTypeInformation -Encoding utf8

Write-Section "GRADLE SETTINGS"
Get-ChildItem -Recurse -Force -Filter settings.gradle* |
  Select-Object FullName | Out-File (Join-Path $out "settings_files.txt") -Encoding utf8

Get-ChildItem -Recurse -Force -Filter build.gradle* |
  Select-Object FullName | Out-File (Join-Path $out "build_files.txt") -Encoding utf8

Get-ChildItem -Recurse -Force -Filter gradle.properties |
  Select-Object FullName | Out-File (Join-Path $out "gradle_properties_files.txt") -Encoding utf8

Write-Section "GRADLE PROJECTS & TASKS"
.\gradlew projects --no-configuration-cache 2>&1 | Out-File (Join-Path $out "gradle_projects.txt") -Encoding utf8
.\gradlew tasks --all --no-configuration-cache 2>&1 | Out-File (Join-Path $out "gradle_tasks_all.txt") -Encoding utf8

Write-Section "DEPENDENCIES (PER MODULE)"
# Lista de módulos detectados desde "gradle projects"
$projectsText = Get-Content (Join-Path $out "gradle_projects.txt") -Raw
$modules = @()
$projectsText -split "`n" | ForEach-Object {
  if ($_ -match "Project ':(.+)'") { $modules += $Matches[1] }
}
if ($modules.Count -eq 0) {
  # fallback: intenta listar usando carpetas típicas
  $modules = Get-ChildItem -Directory | Select-Object -ExpandProperty Name
}

foreach ($m in $modules) {
  $file = Join-Path $out ("deps_{0}.txt" -f $m.Replace(":", "_"))
  try {
    .\gradlew (":$m:dependencies") --configuration runtimeClasspath --no-configuration-cache 2>&1 | Out-File $file -Encoding utf8
  } catch {
    # no todos los módulos tienen esa config
    "FAILED deps for :$m -> $($_.Exception.Message)" | Out-File $file -Encoding utf8
  }
}

Write-Section "TEST INVENTORY"
Get-ChildItem -Recurse -Force -Path "." -Include "*Test.java","*Tests.java","*IT.java","*E2E.java" |
  Select-Object FullName |
  Out-File (Join-Path $out "tests_list.txt") -Encoding utf8

Write-Section "SPRING DUPLICATES & ANNOTATIONS"
# Controllers / RestControllerAdvice / Configuration / Bean
Get-ChildItem -Recurse -Force -Path "." -Filter "*.java" |
  Select-String "@RestController|@Controller|@RestControllerAdvice|@Configuration|@Bean" |
  Select-Object Path, LineNumber, Line |
  Out-File (Join-Path $out "spring_annotations.txt") -Encoding utf8

# Duplicados por nombre de clase (simple pero útil)
Get-ChildItem -Recurse -Force -Path "." -Filter "*.java" |
  Select-String "class\s+[A-Za-z0-9_]+\b" |
  ForEach-Object {
    if ($_.Line -match "class\s+([A-Za-z0-9_]+)\b") {
      [PSCustomObject]@{ Class=$Matches[1]; Path=$_.Path; Line=$_.LineNumber }
    }
  } |
  Group-Object Class |
  Where-Object { $_.Count -gt 1 } |
  ForEach-Object {
    "CLASS DUPLICATE: $($_.Name) (x$($_.Count))" 
    $_.Group | ForEach-Object { "  - $($_.Path):$($_.Line)" }
    ""
  } | Out-File (Join-Path $out "duplicate_class_names.txt") -Encoding utf8

Write-Section "RUN ALL TESTS (SMOKE)"
# Esto NO reemplaza CI, pero deja evidencia local. Si falla, lo deja en output.
try {
  .\gradlew test --no-configuration-cache 2>&1 | Out-File (Join-Path $out "gradle_test_run.txt") -Encoding utf8
} catch {
  "TESTS FAILED. See gradle_test_run.txt and build/reports." | Out-File (Join-Path $out "gradle_test_run.txt") -Append -Encoding utf8
}

Write-Section "DONE"
"Diagnostics written to: $out" | Out-File (Join-Path $out "_done.txt") -Encoding utf8
Write-Host "OK -> Diagnostics written to $out"
