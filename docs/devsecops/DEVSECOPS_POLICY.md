# ILP Enterprise DevSecOps Policy

## Required Gates

Every pull request must pass:

- Compile
- Unit tests
- Integration tests when available
- Linting
- SonarCloud
- Dependency scanning
- Secret scanning

## Security Rules

- No secrets in repository
- No critical dependency vulnerabilities
- No disabled security filters without ADR
- MFA required for privileged users
- JWT and refresh token changes require security review

## Branch Strategy

- main
- feat/*
- fix/*
- hotfix/*
- release/*

## Pull Request Rules

- Linked Azure Boards item
- Peer review
- Green pipeline
- Updated tests
- Updated documentation when needed
