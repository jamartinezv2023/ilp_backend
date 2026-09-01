$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$Expected = @{
    "ILP-EPL" = @{ File="ilp-epl-v0.1.0-candidate.json"; Items=24; Dimensions=4; Scale="likert_single_choice" }
    "ILP-MEA" = @{ File="ilp-mea-v0.1.0-candidate.json"; Items=30; Dimensions=5; Scale="frequency_single_choice" }
    "ILP-IVP" = @{ File="ilp-ivp-v0.1.0-candidate.json"; Items=30; Dimensions=6; Scale="interest_single_choice" }
}
$AllIds = New-Object System.Collections.Generic.List[string]
$AllTexts = New-Object System.Collections.Generic.List[string]
foreach ($Id in $Expected.Keys) {
    $Rule = $Expected[$Id]
    $Spec = Get-Content -LiteralPath (Join-Path $Root $Rule.File) -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($Spec.instrumentId -ne $Id) { throw "$Id identity failed." }
    if (@($Spec.items).Count -ne $Rule.Items) { throw "$Id item count failed." }
    if (@($Spec.dimensions).Count -ne $Rule.Dimensions) { throw "$Id dimension count failed." }
    if ($Spec.responseScale.type -ne $Rule.Scale) { throw "$Id response scale failed." }
    if ($Spec.status -ne "candidate_not_validated") { throw "$Id status failed." }
    if ($Spec.scoring.globalScoreEnabled -ne $false -or $Spec.scoring.categoricalBandsEnabled -ne $false) { throw "$Id scoring safety failed." }
    $DimensionCodes = @($Spec.dimensions | ForEach-Object code)
    foreach ($Item in $Spec.items) {
        if ($AllIds.Contains([string]$Item.id)) { throw "Duplicate item ID: $($Item.id)" }
        if ($AllTexts.Contains(([string]$Item.text).Trim().ToLowerInvariant())) { throw "Duplicate item text: $($Item.id)" }
        if ($DimensionCodes -notcontains $Item.dimension) { throw "Unknown dimension: $($Item.id)" }
        $AllIds.Add([string]$Item.id)
        $AllTexts.Add(([string]$Item.text).Trim().ToLowerInvariant())
    }
}
if ($AllIds.Count -ne 84) { throw "Total item count failed." }
Write-Host "INSTRUMENT_INTEGRITY_TESTS=PASS"
Write-Host "INSTRUMENTS=3"
Write-Host "ITEMS=84"
exit 0