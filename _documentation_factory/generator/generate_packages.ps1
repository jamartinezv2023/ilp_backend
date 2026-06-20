$catalogPath = "_documentation_factory\catalogs\phase_catalog.csv"
$base = "_doctoral_review_package"

$items = Import-Csv $catalogPath

foreach ($item in $items) {
    $folderName = "$($item.PHASE)$($item.PACKAGE.Substring(1))_$($item.FOLDER -replace ' ','_' -replace '[^a-zA-Z0-9_]', '')"
    $path = Join-Path $base $folderName

    New-Item -ItemType Directory -Force $path | Out-Null

    @"
# $($item.PHASE)$($item.PACKAGE.Substring(1)) — $($item.FOLDER)

## Purpose

$($item.PURPOSE)

## Scientific Role

This package supports the transition from scientific infrastructure to operational evidence production.

## Expected Output

A reproducible, versioned, auditable package connected to datasets, experiments, papers and doctoral evidence.
"@ | Set-Content "$path\01_overview.md" -Encoding utf8

    @"
ITEM,STATUS
Overview,PENDING
Dataset traceability,PENDING
Experiment traceability,PENDING
Quality criteria,PENDING
Ethics alignment,PENDING
Reproducibility,PENDING
Paper mapping,PENDING
Review readiness,PENDING
"@ | Set-Content "$path\02_checklist.csv" -Encoding utf8

    @"
ARTIFACT,DESCRIPTION
Dataset,Versioned scientific data source
Notebook,Reproducible analysis workflow
Experiment,Registered scientific experiment
Figure,Generated visual evidence
Table,Generated analytical evidence
Paper,Associated publication output
"@ | Set-Content "$path\03_artifact_matrix.csv" -Encoding utf8

    @"
TRACE_LEVEL,VALUE
Phase,$($item.PHASE)
Package,$($item.PACKAGE)
Folder,$folderName
Purpose,$($item.PURPOSE)
Status,DESIGN
"@ | Set-Content "$path\04_traceability.csv" -Encoding utf8

    @"
# Reproducibility Notes

Each future result generated from this package must include:

- dataset version
- code commit
- notebook version
- experiment id
- random seed
- metrics artifact
- figure artifact
- paper mapping
"@ | Set-Content "$path\05_reproducibility.md" -Encoding utf8
}

Write-Host "Documentation Factory completed successfully."
