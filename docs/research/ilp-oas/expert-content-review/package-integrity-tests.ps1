$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$ExpectedFiles = @(
    "README.md","construct-dimension-reference.csv","expert-eligibility-conflict-and-independence-form.md",
    "expert-item-rating-form.csv","expert-review-analysis.ps1","expert-review-protocol.md",
    "global-instrument-evaluation-form.csv","item-decision-rules.md","package-integrity-tests.ps1",
    "review-data-dictionary.md","rights-authorship-and-originality-audit.csv"
)
$ActualFiles = @(Get-ChildItem -LiteralPath $Root -File | Select-Object -ExpandProperty Name | Sort-Object)
if (($ActualFiles -join "`n") -ne (($ExpectedFiles | Sort-Object) -join "`n")) { throw "Package file set failed." }
$Ratings = @(Import-Csv -LiteralPath (Join-Path $Root "expert-item-rating-form.csv") -Encoding UTF8)
$Dimensions = @(Import-Csv -LiteralPath (Join-Path $Root "construct-dimension-reference.csv") -Encoding UTF8)
$Global = @(Import-Csv -LiteralPath (Join-Path $Root "global-instrument-evaluation-form.csv") -Encoding UTF8)
$Rights = @(Import-Csv -LiteralPath (Join-Path $Root "rights-authorship-and-originality-audit.csv") -Encoding UTF8)
if ($Ratings.Count -ne 84 -or $Dimensions.Count -ne 15 -or $Global.Count -ne 3 -or $Rights.Count -ne 84) { throw "Package row cardinality failed." }
$Ids = @($Ratings.ItemId | Sort-Object -Unique)
if ($Ids.Count -ne 84) { throw "Rating item identity failed." }
$CompletedRatings = @($Ratings | Where-Object {
    -not [string]::IsNullOrWhiteSpace($_.ReviewerCode) -or
    -not [string]::IsNullOrWhiteSpace($_.Relevance1to4) -or
    -not [string]::IsNullOrWhiteSpace($_.Recommendation)
})
if ($CompletedRatings.Count -ne 0) { throw "The package is not an empty review template." }
$CompletedRights = @($Rights | Where-Object { $_.RightsDecision -ne "PENDING" -or $_.LicenseDecision -ne "PENDING" })
if ($CompletedRights.Count -ne 0) { throw "Rights decisions were prematurely recorded." }
$ParserTokens=$null;$ParserErrors=$null
[void][Management.Automation.Language.Parser]::ParseFile((Join-Path $Root "expert-review-analysis.ps1"),[ref]$ParserTokens,[ref]$ParserErrors)
if (@($ParserErrors).Count -ne 0) { throw "Analysis script parser certification failed." }
Write-Host "EXPERT_REVIEW_PACKAGE_TESTS=PASS"
Write-Host "INSTRUMENTS=3"
Write-Host "DIMENSIONS=15"
Write-Host "ITEMS=84"
Write-Host "COMPLETED_RATINGS=0"
Write-Host "RIGHTS_DECISIONS=0"
exit 0