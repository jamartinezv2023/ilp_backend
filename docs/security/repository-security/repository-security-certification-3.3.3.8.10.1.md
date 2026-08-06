# Repository Security Certification 3.3.3.8.10.1

## Incident

GitHub Push Protection detected an Azure DevOps personal access
token in a locally generated baseline artifact.

## Affected path

_phase_1a_assessment_engine/00_baseline/git_remotes.txt

## Remediation

- The exposed PAT was treated as compromised.
- The PAT was revoked in Azure DevOps.
- A protected local backup was created.
- The affected path was removed from branch history.
- Reachable Git objects were inspected.
- Repository ignore rules were strengthened.
- The branch was republished through GitHub Push Protection.

## Prohibited evidence

The token value is not reproduced in this document.

## Acceptance criteria

- Sensitive path absent from reachable branch history.
- No push-protection rejection.
- Canonical GitHub Actions workflow passes.
- Repository working tree is clean.
- Security documentation is committed.
- Backup containing contaminated history is securely deleted after
  certification.