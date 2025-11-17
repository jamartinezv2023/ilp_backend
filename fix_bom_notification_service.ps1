Write-Host "==============================================="
Write-Host "CLEAN BOM IN NOTIFICATION-SERVICE"
Write-Host "==============================================="

$NotificationPath = "notification-service/src/main/java"

Write-Host "Scanning path:"
Write-Host $NotificationPath

$files = Get-ChildItem -Path $NotificationPath -Recurse -Filter *.java

foreach ($file in $files) {

    Write-Host "Checking: $($file.FullName)"

    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)

    if ($bytes.Length -ge 3 -and 
        $bytes[0] -eq 0xEF -and 
        $bytes[1] -eq 0xBB -and 
        $bytes[2] -eq 0xBF) {

        Write-Host "BOM FOUND - FIXING"

        $cleanBytes = $bytes[3..($bytes.Length - 1)]
        [System.IO.File]::WriteAllBytes($file.FullName, $cleanBytes)
    }
    else {
        Write-Host "NO BOM"
    }
}

Write-Host "==============================================="
Write-Host "Recompiling notification-service..."
Write-Host "==============================================="

mvn clean package -pl notification-service -am

Write-Host "==============================================="
Write-Host "BOM CLEAN COMPLETE"
Write-Host "==============================================="
