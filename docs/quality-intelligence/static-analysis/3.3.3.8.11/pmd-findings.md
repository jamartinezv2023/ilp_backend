# PMD Findings

## Cycle

3.3.3.8.11 - Static Analysis Certification & Release Candidate
## Toolchain

- Initial PMD version: 7.6.0
- Certified PMD version: 7.26.0
- Java: 17
- Gradle: 8.6

## Parser compatibility

PMD 7.6.0 produced the following internal parser exception:

`Invalid type reference for method or ctor type annotation: 19`

PMD 7.26.0 completed without parser exceptions under Java 17 and
Gradle 8.6.

## Findings

Two `NullAssignment` findings were originally reported in
`ScientificFeatureGenerationRunEntity`.

### complete()

The assignment `errorMessage = null` is intentional. A successfully
completed scientific feature-generation run cannot retain failure
metadata from an earlier state.

The suppression is local to the `complete()` method:

`@SuppressWarnings("PMD.NullAssignment")`

### fail()

The assignment `featureVector = null` is intentional. A failed
scientific feature-generation run cannot retain a feature vector as a
successful result.

The suppression is local to the `fail()` method:

`@SuppressWarnings("PMD.NullAssignment")`

## Suppression policy

- Global PMD suppression: prohibited.
- Ruleset-wide exclusion: prohibited without architectural approval.
- Method-level suppression: permitted only with explicit semantic
  justification.
- Every suppression must preserve domain invariants and be covered by
  tests.

## Certification result

- Parser exceptions: 0
- Main violations: 0
- Test violations: 0
- Global suppressions: 0
- Local justified suppressions: 2
- PMD blocking mode: enabled
- Certification status: PASS