<# 
    File: free-port.ps1
    Location: C:\temp\ilp-rebuild\inclusive-learning-platform\
    Author: Inclusive Learning Platform Automation
    Description:
        Detects and frees ports blocked by other processes before running Docker containers.
        Supports interactive and automatic modes (use -Force for CI/CD).
#>

param (
    [Parameter(Mandatory = $false, ValueFromRemainingArguments = $true)]
    [int[]] $Ports = @(8761, 8888, 8080, 8081, 8082, 5432),

    [switch] $Force
)

Write-Host "🔍 Checking ports: $($Ports -join ', ')" -ForegroundColor Cyan

foreach ($port in $Ports) {
    $netstatOutput = netstat -ano | findstr ":$port"
    if ($netstatOutput) {
        Write-Host "`n⚠️  Port $port is currently in use:`n" -ForegroundColor Yellow
        $lines = $netstatOutput -split "`n"
        foreach ($line in $lines) {
            if ($line -match '\s+(\d+)$') {
                $pid = [int]$matches[1]
                try {
                    $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
                    $procName = if ($process) { $process.ProcessName } else { "Unknown" }

                    Write-Host "   PID: $pid  |  Process: $procName" -ForegroundColor Gray

                    if ($Force) {
                        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
                        Write-Host "   ✅ Automatically freed port $port (PID $pid stopped)" -ForegroundColor Green
                    } else {
                        $confirm = Read-Host "   ❓ Do you want to terminate this process to free port $port? (y/n)"
                        if ($confirm -eq 'y') {
                            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
                            Write-Host "   ✅ Port $port freed (PID $pid stopped)" -ForegroundColor Green
                        } else {
                            Write-Host "   ⏸️ Port $port left occupied" -ForegroundColor DarkYellow
                        }
                    }
                } catch {
                    Write-Host "   ❌ Could not terminate process with PID $pid" -ForegroundColor Red
                }
            }
        }
    } else {
        Write-Host "✅ Port $port is free" -ForegroundColor Green
    }
}

Write-Host "`n✨ Port check and cleanup completed." -ForegroundColor Cyan
