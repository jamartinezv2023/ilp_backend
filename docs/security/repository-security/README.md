# Repository Security

## Rules

- Never commit personal access tokens.
- Never store credentials in diagnostic snapshots.
- Remote URLs must omit embedded usernames and tokens.
- Secrets must be stored in GitHub Actions Secrets or the approved
  institutional secret manager.
- Files containing environment diagnostics must redact credentials.
- A leaked credential must be revoked before history remediation.
- Push Protection findings must not be bypassed without a documented,
  approved false-positive assessment.

## Safe remote evidence

Allowed:

origin https://github.com/organization/repository.git

Forbidden:

https://username:token@example.com/repository.git

## Incident response

1. Revoke or rotate the credential.
2. Identify every affected commit and ref.
3. Remove the secret from history.
4. verify the rewritten history.
5. Push the sanitized refs.
6. Validate secret scanning and CI.
7. Document the incident without recording the secret value.