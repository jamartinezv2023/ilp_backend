param(
    [Parameter(Mandatory=$true)][string] $RatingsPath,
    [Parameter(Mandatory=$true)][string] $OutputDirectory
)
$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

function Get-Factorial {
    param([int] $N)
    [double] $Result = 1
    for ($Index = 2; $Index -le $N; $Index++) { $Result *= $Index }
    return $Result
}

function Get-Combination {
    param([int] $N, [int] $K)
    if ($K -lt 0 -or $K -gt $N) { return 0 }
    return (Get-Factorial $N) / ((Get-Factorial $K) * (Get-Factorial ($N - $K)))
}

$Criteria = @(
    "Relevance1to4", "Clarity1to4", "Comprehensibility1to4",
    "Representativeness1to4", "Accessibility1to4", "CulturalAppropriateness1to4"
)
$Rows = @(Import-Csv -LiteralPath $RatingsPath -Encoding UTF8)
if ($Rows.Count -eq 0) { throw "No completed rating rows were supplied." }
$DuplicateKeys = @($Rows | Group-Object { "$($_.ReviewRound)|$($_.ReviewerCode)|$($_.InstrumentId)|$($_.Version)|$($_.ItemId)" } | Where-Object Count -gt 1)
if ($DuplicateKeys.Count -ne 0) { throw "Duplicate reviewer-item rows were found." }
foreach ($Row in $Rows) {
    if ([string]::IsNullOrWhiteSpace($Row.ReviewRound) -or [string]::IsNullOrWhiteSpace($Row.ReviewerCode)) {
        throw "ReviewRound and ReviewerCode are required in completed data."
    }
    foreach ($Criterion in $Criteria) {
        [int] $Value = 0
        if (-not [int]::TryParse([string]$Row.$Criterion, [ref]$Value) -or $Value -lt 1 -or $Value -gt 4) {
            throw "Invalid $Criterion value for $($Row.ItemId)."
        }
    }
}
New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$ItemResults = @(
    foreach ($Group in ($Rows | Group-Object InstrumentId,Version,ItemId)) {
        $First = $Group.Group[0]
        foreach ($Criterion in $Criteria) {
            $N = $Group.Count
            $Agreement = @($Group.Group | Where-Object { [int] $_.$Criterion -ge 3 }).Count
            $ICvi = $Agreement / [double] $N
            $Chance = (Get-Combination $N $Agreement) * [Math]::Pow(0.5, $N)
            $Kappa = if ((1 - $Chance) -eq 0) { 1 } else { ($ICvi - $Chance) / (1 - $Chance) }
            [pscustomobject]@{
                InstrumentId=$First.InstrumentId;Version=$First.Version;ItemId=$First.ItemId
                Criterion=$Criterion;Reviewers=$N;Agreement3or4=$Agreement
                ICVI=[Math]::Round($ICvi,4);ChanceAgreement=[Math]::Round($Chance,6)
                ModifiedKappa=[Math]::Round($Kappa,4)
            }
        }
    }
)
$ItemResults | Export-Csv -LiteralPath (Join-Path $OutputDirectory "item-content-validity-summary.csv") -NoTypeInformation -Encoding UTF8
$ScaleResults = @(
    foreach ($Group in ($ItemResults | Group-Object InstrumentId,Version,Criterion)) {
        [pscustomobject]@{
            InstrumentId=$Group.Group[0].InstrumentId;Version=$Group.Group[0].Version
            Criterion=$Group.Group[0].Criterion;Items=$Group.Count
            SCVIAverage=[Math]::Round((($Group.Group | Measure-Object ICVI -Average).Average),4)
        }
    }
)
$ScaleResults | Export-Csv -LiteralPath (Join-Path $OutputDirectory "scale-content-validity-summary.csv") -NoTypeInformation -Encoding UTF8
Write-Host "EXPERT_REVIEW_ANALYSIS=PASS"
Write-Host "RATING_ROWS=$($Rows.Count)"
Write-Host "ITEM_CRITERION_RESULTS=$($ItemResults.Count)"