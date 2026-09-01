$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$ExpectedFiles = @(
    "README.md","authorization-register.csv","conflict-independence-disposition-register.csv",
    "data-protection-plan.md","ethics-and-institutional-determination-register.csv",
    "expert-invitation-template.md","governance-integrity-tests.ps1","governance-readiness-checklist.csv",
    "panel-coverage-plan.csv","privacy-and-participation-notice-template.md",
    "regulatory-and-methodological-basis.md","review-analysis-decision-freeze.yml",
    "reviewer-selection-and-exclusion.md","secure-transfer-and-custody-sop.md"
)
$ActualFiles = @(Get-ChildItem -LiteralPath $Root -File | Select-Object -ExpandProperty Name | Sort-Object)
if (($ActualFiles -join "`n") -ne (($ExpectedFiles | Sort-Object) -join "`n")) { throw "Governance file set failed." }
$Checklist = @(Import-Csv -LiteralPath (Join-Path $Root "governance-readiness-checklist.csv") -Encoding UTF8)
$Coverage = @(Import-Csv -LiteralPath (Join-Path $Root "panel-coverage-plan.csv") -Encoding UTF8)
$Conflicts = @(Import-Csv -LiteralPath (Join-Path $Root "conflict-independence-disposition-register.csv") -Encoding UTF8)
$Determinations = @(Import-Csv -LiteralPath (Join-Path $Root "ethics-and-institutional-determination-register.csv") -Encoding UTF8)
$Authorizations = @(Import-Csv -LiteralPath (Join-Path $Root "authorization-register.csv") -Encoding UTF8)
if ($Checklist.Count -ne 12 -or $Coverage.Count -ne 7 -or $Conflicts.Count -ne 10 -or $Determinations.Count -ne 7 -or $Authorizations.Count -ne 5) { throw "Governance row cardinality failed." }
if (@($Coverage | Where-Object CoverageStatus -ne "PENDING").Count -ne 0) { throw "Panel coverage was prematurely claimed." }
if (@($Conflicts | Where-Object { $_.IndependenceStatus -ne "PENDING" -or $_.EligibilityDisposition -ne "PENDING" }).Count -ne 0) { throw "Reviewer eligibility was prematurely claimed." }
if (@($Determinations | Where-Object Status -ne "PENDING").Count -ne 0) { throw "External determination was prematurely claimed." }
if (@($Authorizations | Where-Object Status -ne "BLOCKED").Count -ne 0) { throw "Operational authorization was prematurely opened." }
$Freeze = Get-Content -LiteralPath (Join-Path $Root "review-analysis-decision-freeze.yml") -Raw -Encoding UTF8
foreach ($Required in @(
    "status: PENDING_EXTERNAL_APPROVAL_AND_PREREGISTRATION",
    "final_planned_n: PENDING",
    "criterion_specific_thresholds: PENDING",
    "instruments_analyzed_separately: true",
    "criteria_collapsed_into_global_validity_score: false"
)) {
    if (-not $Freeze.Contains($Required)) { throw "Analysis-freeze safety marker missing: $Required" }
}
Write-Host "EXPERT_REVIEW_GOVERNANCE_TESTS=PASS"
Write-Host "REQUIREMENTS=12"
Write-Host "EXTERNAL_DETERMINATIONS=0"
Write-Host "ELIGIBLE_REVIEWERS=0"
Write-Host "OPEN_AUTHORIZATIONS=0"
exit 0