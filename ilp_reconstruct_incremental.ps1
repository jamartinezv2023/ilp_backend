# =============================================
# ILP - Reconstruction Incremental (Option C)
# =============================================

$modules = @(
    "commons-service",
    "auth-service",
    "notification-service",
    "adaptive-education-service",
    "assessment-service",
    "monitoring-service",
    "report-service",
    "tenant-service",
    "gateway-service"
)

Write-Host ""
Write-Host "==============================================="
Write-Host " ILP - Incremental Reconstruction (Option C)"
Write-Host " Keep working modules, rebuild only failing ones"
Write-Host "==============================================="

foreach ($m in $modules) {

    Write-Host ""
    Write-Host "----------------------------------------------"
    Write-Host " CHECKING MODULE: $m"
    Write-Host "----------------------------------------------"

    $result = mvn clean package -pl $m -am 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Host " OK: $m compiles successfully. Keeping it." -ForegroundColor Green
    }
    else {
        Write-Host " FAIL: $m failed compilation. Applying reconstruction..." -ForegroundColor Yellow

        # 1. Optional BOM cleanup script
        $fixBomScript = ".\fix_bom_$m.ps1"
        if (Test-Path $fixBomScript) {
            Write-Host "   -> Running BOM cleanup for $m..."
            & $fixBomScript
        }

        # 2. Optional CRUD reconstruction script
        $fixCrudScript = ".\fix_${m}_crud.ps1"
        if (Test-Path $fixCrudScript) {
            Write-Host "   -> Rebuilding CRUD base for $m..."
            & $fixCrudScript
        }
        else {
            Write-Host "   WARNING: No CRUD rebuild script found for $m"
        }

        # 3. Retry compilation
        Write-Host "   -> Retrying compilation..."
        $retry = mvn clean package -pl $m -am 2>&1

        if ($LASTEXITCODE -eq 0) {
            Write-Host " SUCCESS: $m rebuilt and compiles OK." -ForegroundColor Green
        }
        else {
            Write-Host " ERROR: $m still fails. Manual intervention required." -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "==============================================="
Write-Host " ILP Incremental Reconstruction Completed"
Write-Host "==============================================="
