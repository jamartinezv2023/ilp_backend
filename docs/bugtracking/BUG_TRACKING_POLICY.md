# ILP Enterprise Bug Tracking Policy

## Purpose

This document defines the enterprise bug tracking process for the ILP platform.

## Bug Classification

### Severity

| Level | Meaning |
|---|---|
| Sev0 | Complete outage |
| Sev1 | Critical security or data-loss issue |
| Sev2 | Broken core functionality |
| Sev3 | Partial degradation |
| Sev4 | Cosmetic issue |

## Required Bug Fields

Every bug must include:

- Summary
- Steps to reproduce
- Expected result
- Actual result
- Severity
- Priority
- Environment
- Logs or evidence
- Screenshots when applicable
- Regression impact
- Root cause analysis when Sev0 or Sev1

## Standard Tags

- security
- regression
- frontend
- backend
- devops
- testing
- accessibility
- performance
- offline-first
- technical-debt
- legacy
- observability

## Workflow

New ? Active ? Resolved ? Closed

## Enterprise Rule

No Sev0 or Sev1 bug can be closed without:
- root cause analysis
- regression test
- remediation notes
- owner approval
