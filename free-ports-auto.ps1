# ======================================================
# 🧰 free-ports-auto.ps1
# Libera puertos ocupados (Windows + Docker)
# Uso:
#   .\free-ports-auto.ps1 8761 8888 8081 [-Force]
# ======================================================

param(
    [Parameter(Mandatory=$true, Position=0)]
    [int[]]$Ports,

    [switch]$Force
)

Write-Host "🔍 Escaneando puertos: $($Ports -join ', ')..." -ForegroundColor Cyan

foreach ($port in $Ports) {
    Write-Host "`n➡️  Puerto $port" -ForegroundColor Yellow

    # Buscar procesos normales
    $connections = netstat -ano | findstr ":$port" | ForEach-Object {
        ($_ -split '\s+')[-1]
    } | Sort-Object -Unique

    if ($connections) {
        Write-Host "⚠️  Encontrado(s) proceso(s): $($connections -join ', ')" -ForegroundColor Red
        foreach ($pid in $connections) {
            try {
                if ($Force) {
                    Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
                    Write-Host "✅ Proceso $pid detenido." -ForegroundColor Green
                } else {
                    $confirm = Read-Host "¿Deseas detener el proceso $pid? (s/n)"
                    if ($confirm -eq 's') {
                        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
                        Write-Host "✅ Proceso $pid detenido." -ForegroundColor Green
                    } else {
                        Write-Host "⏭️  Proceso $pid no detenido." -ForegroundColor Yellow
                    }
                }
            } catch {
                Write-Host "❌ Error al detener el proceso $pid" -ForegroundColor Red
            }
        }
    } else {
        Write-Host "🟢 Puerto $port libre en el sistema." -ForegroundColor Green
    }

    # Buscar contenedores Docker que usen el puerto
    $dockerContainers = docker ps --format "{{.ID}} {{.Ports}}" | Select-String ":$port"
    if ($dockerContainers) {
        Write-Host "🐳 Puerto $port en uso por contenedor(es):" -ForegroundColor Cyan
        $dockerContainers | ForEach-Object {
            $containerId = ($_ -split ' ')[0]
            if ($Force) {
                docker stop $containerId | Out-Null
                Write-Host "✅ Contenedor detenido: $containerId" -ForegroundColor Green
            } else {
                $confirm = Read-Host "¿Deseas detener el contenedor $containerId? (s/n)"
                if ($confirm -eq 's') {
                    docker stop $containerId | Out-Null
                    Write-Host "✅ Contenedor detenido: $containerId" -ForegroundColor Green
                } else {
                    Write-Host "⏭️  Contenedor $containerId no detenido." -ForegroundColor Yellow
                }
            }
        }
    }
}

Write-Host "`n🎯 Escaneo y liberación completados." -ForegroundColor Magenta
