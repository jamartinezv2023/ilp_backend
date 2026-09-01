$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$ExpectedFiles = @(
    "README.md","analysis-decision-freeze-v1.yml","authorization-decision-template.md",
    "data-protection-impact-screening.md","document-submission-index.csv",
    "ethics-institutional-cover-letter-template.md","ethics-review-application-summary.md",
    "external-evidence-intake-register.csv","package-integrity-tests.ps1","preregistration-draft.md",
    "risk-and-applicability-assessment.md","synthetic-expert-ratings.csv",
    "synthetic-review-dry-run.ps1","transparency-and-deviation-policy.md"
)
$ActualFiles = @(Get-ChildItem -LiteralPath $Root -File | Select-Object -ExpandProperty Name | Sort-Object)
if (($ActualFiles -join "`n") -ne (($ExpectedFiles | Sort-Object) -join "`n")) { throw "A8 package file set failed." }
$Synthetic = @(Import-Csv -LiteralPath (Join-Path $Root "synthetic-expert-ratings.csv") -Encoding UTF8)
$Evidence = @(Import-Csv -LiteralPath (Join-Path $Root "external-evidence-intake-register.csv") -Encoding UTF8)
$Submission = @(Import-Csv -LiteralPath (Join-Path $Root "document-submission-index.csv") -Encoding UTF8)
if ($Synthetic.Count -ne 672 -or $Evidence.Count -ne 8 -or $Submission.Count -ne 12) { throw "A8 row cardinality failed." }
if (@($Synthetic | Where-Object { $_.ReviewRound -ne "SYNTHETIC-DRY-RUN" -or $_.ReviewerCode -notmatch "^SYN-[0-9]{2}$" }).Count -ne 0) { throw "A8 synthetic identity safety failed." }
if (@($Evidence | Where-Object Status -ne "PENDING").Count -ne 0) { throw "External evidence was prematurely claimed." }
$Freeze = Get-Content -LiteralPath (Join-Path $Root "analysis-decision-freeze-v1.yml") -Raw -Encoding UTF8
foreach ($Marker in @(
    "status: FROZEN_BEFORE_REAL_EXPERT_RATINGS",
    "planned_eligible_independent_reviewers: 8",
    "minimum_valid_ratings_per_item: 6",
    "item_cvi_minimum: 0.78",
    "scale_cvi_average_target: 0.90",
    "imputation: NONE",
    "submission_status: NOT_SUBMITTED"
)) { if (-not $Freeze.Contains($Marker)) { throw "Analysis marker missing: $Marker" } }
Write-Host "A8_PACKAGE_INTEGRITY_TESTS=PASS"
Write-Host "FILES=14"
Write-Host "SYNTHETIC_ROWS=672"
Write-Host "EXTERNAL_EVIDENCE_RECORDED=0"
Write-Host "PREREGISTRATION_SUBMITTED=False"
exit 0