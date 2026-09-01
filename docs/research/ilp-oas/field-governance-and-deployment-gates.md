# Field governance and deployment gates — v0.2.0 candidate

Field readiness: **Blocked** until every mandatory gate is evidenced and independently certified.

## Mandatory gates before field collection

1. Supervisor-approved protocol and version.
2. Competent ethics approval recorded with conditions.
3. Institutional authorization for each participating site.
4. Final consent/assent and guardian pathway.
5. Data-management plan: minimization, roles, retention, deletion, incidents and controlled access.
6. Frozen instrument, scoring, renderer, API schema and consent versions.
7. Expert content review and cognitive-interview evidence.
8. Accessibility and language review.
9. Security, backup, rollback and recovery certification.
10. Pilot authorization explicitly separate from main validation.

## Continuous-deployment contract

An active study references immutable instrument and scoring versions. A deployment must fail closed if it would alter active questions, response options, required fields, scoring, consent text or identifiers. Changes require a new version, compatibility assessment, migration test, rollback plan and research authorization. Production health never equals research validity.

## Data release classes

- Open: protocols, synthetic examples, metadata, analysis code and researcher-owned materials approved for release.
- Controlled: de-identified or pseudonymized data whose disclosure risk requires approved access.
- Restricted: identity keys, consent records, security material and sensitive individual-level information.

Open science is as open as possible and as restricted as necessary to protect participants, privacy, rights and research integrity.