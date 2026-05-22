# Enterprise Pull Request Checklist

## Governance

- [ ] Linked Azure Boards item
- [ ] Acceptance criteria validated
- [ ] ADR updated if architecture changed
- [ ] Documentation updated

## Quality

- [ ] Unit tests added or updated
- [ ] Integration tests validated
- [ ] Build passes
- [ ] SonarCloud passes
- [ ] No critical vulnerabilities

## Security

- [ ] No secrets committed
- [ ] JWT/MFA reviewed if applicable
- [ ] Security implications reviewed

## Observability

- [ ] Logging validated
- [ ] Metrics considered
- [ ] Error handling reviewed

## Release

- [ ] Backward compatibility validated
- [ ] Rollback strategy considered

## Evidence

Attach:
- screenshots
- logs
- test evidence
- pipeline evidence
