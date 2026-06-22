$base = "_doctoral_review_package"

if (!(Test-Path $base)) {
    New-Item -ItemType Directory -Force $base | Out-Null
}

$folders = Get-ChildItem $base -Directory | Sort-Object Name

foreach ($folder in $folders) {

    $name = $folder.Name
    $path = $folder.FullName

    $phasePrefix = ($name -split "_")[0]

    @"
# $name

## Purpose

This package belongs to the ILP scientific, ethical, architectural, operational, implementation, publication or legacy lifecycle.

## Strategic Role

It supports the transformation of ILP from a software platform into a reproducible scientific infrastructure for inclusive education, longitudinal datasets, machine learning, explainable AI, offline-first operation and doctoral research.

## Current Status

POPULATED_BY_MASTER_FACTORY

## Review Requirement

This package must be reviewed before being used as evidence for fieldwork, datasets, machine learning experiments, publications or doctoral defense.
"@ | Set-Content "$path\00_phase_overview.md" -Encoding utf8

    @"
FIELD,VALUE
Package,$name
PhasePrefix,$phasePrefix
Repository,ilp_backend
GeneratedBy,master_populate_all_phases_complete
Lifecycle,Scientific and technical research infrastructure
Status,POPULATED
ReviewRequired,YES
TraceabilityRequired,YES
"@ | Set-Content "$path\00_phase_metadata.csv" -Encoding utf8

    @"
OBJECTIVE,DESCRIPTION
ScientificTraceability,Connect this package to research evidence
EthicsTraceability,Ensure ethical and consent dependencies are visible
DatasetTraceability,Connect records to dataset versions when applicable
ImplementationTraceability,Connect plans to code commits and PRs when applicable
PublicationTraceability,Connect outputs to papers and doctoral compendium
"@ | Set-Content "$path\01_traceability_objectives.csv" -Encoding utf8

    @"
ARTIFACT,REQUIRED
Overview,YES
Metadata,YES
Checklist,YES
Traceability matrix,YES
Risk matrix,YES
Evidence register,YES
Review decision,YES
Closure note,YES
"@ | Set-Content "$path\02_required_artifacts.csv" -Encoding utf8

    @"
RISK,MITIGATION
IncompleteDocumentation,Populate required documents and review before use
WeakTraceability,Link each artifact to requirement dataset experiment or paper
EthicsGap,Do not use for fieldwork without ethics approval
DataQualityGap,Do not export datasets without validation rules
ImplementationRisk,Use feature flags tests and rollback strategy
PublicationOverclaiming,Report only evidence supported by data
"@ | Set-Content "$path\03_risk_matrix.csv" -Encoding utf8

    @"
EVIDENCE_TYPE,EXPECTED_SOURCE
GitCommit,Version control history
PullRequest,Review and integration evidence
BuildReport,CI or local build output
TestReport,Unit integration E2E or QA report
DatasetManifest,Versioned dataset snapshot
AuditLog,Traceability and governance event
EthicsEvidence,Approval consent or governance documentation
PublicationArtifact,Paper table figure or supplementary material
"@ | Set-Content "$path\04_evidence_register.csv" -Encoding utf8

    @"
QUALITY_GATE,REQUIRED_STATUS
Completeness,DONE
Traceability,DONE
EthicsDependency,IDENTIFIED
DatasetDependency,IDENTIFIED_WHEN_APPLICABLE
ImplementationDependency,IDENTIFIED_WHEN_APPLICABLE
ReviewReadiness,READY
"@ | Set-Content "$path\05_quality_gates.csv" -Encoding utf8

    @"
DECISION,MEANING
APPROVED,Package may be used as supporting evidence
APPROVED_WITH_OBSERVATIONS,Package may be used after minor corrections
REQUIRES_CORRECTION,Package must be corrected before use
REJECTED,Package is not valid as evidence
"@ | Set-Content "$path\06_review_decision_catalog.csv" -Encoding utf8

    @"
ITEM,STATUS
Folder exists,DONE
Overview created,DONE
Metadata created,DONE
Traceability objectives created,DONE
Required artifacts created,DONE
Risk matrix created,DONE
Evidence register created,DONE
Quality gates created,DONE
Review catalog created,DONE
Ready for audit,DONE
"@ | Set-Content "$path\99_master_checklist.csv" -Encoding utf8
}

@"
# ILP Master Population Report

All existing folders inside _doctoral_review_package were populated with a complete master documentation layer.

Generated files per folder:

- 00_phase_overview.md
- 00_phase_metadata.csv
- 01_traceability_objectives.csv
- 02_required_artifacts.csv
- 03_risk_matrix.csv
- 04_evidence_register.csv
- 05_quality_gates.csv
- 06_review_decision_catalog.csv
- 99_master_checklist.csv

This does not replace phase-specific documents. It adds a consistent audit and traceability layer across all existing phases.
"@ | Set-Content "$base\00_MASTER_PHASE_POPULATION_REPORT.md" -Encoding utf8

Write-Host "All existing phases populated with master documentation layer."
