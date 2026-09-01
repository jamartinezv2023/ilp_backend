param(
    [Parameter(Mandatory=$true)][string] $AnalysisScript,
    [Parameter(Mandatory=$true)][string] $SyntheticRatings,
    [Parameter(Mandatory=$true)][string] $OutputDirectory
)
$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest
$Rows = @(Import-Csv -LiteralPath $SyntheticRatings -Encoding UTF8)
if ($Rows.Count -ne 672) { throw "Synthetic row count failed." }
if (@($Rows | Where-Object { $_.ReviewRound -ne "SYNTHETIC-DRY-RUN" -or $_.ReviewerCode -notmatch "^SYN-[0-9]{2}$" }).Count -ne 0) {
    throw "Non-synthetic identity marker detected."
}
& powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File $AnalysisScript -RatingsPath $SyntheticRatings -OutputDirectory $OutputDirectory
if ($LASTEXITCODE -ne 0) { throw "Synthetic analysis failed." }
$ItemSummary = @(Import-Csv -LiteralPath (Join-Path $OutputDirectory "item-content-validity-summary.csv") -Encoding UTF8)
$ScaleSummary = @(Import-Csv -LiteralPath (Join-Path $OutputDirectory "scale-content-validity-summary.csv") -Encoding UTF8)
if ($ItemSummary.Count -ne 504 -or $ScaleSummary.Count -ne 18) { throw "Synthetic analysis output cardinality failed." }
Write-Host "SYNTHETIC_EXPERT_REVIEW_DRY_RUN=PASS"
Write-Host "SYNTHETIC_RATING_ROWS=672"
Write-Host "ITEM_CRITERION_RESULTS=504"
Write-Host "SCALE_CRITERION_RESULTS=18"
Write-Host "REAL_EXPERT_DATA_USED=False"
exit 0