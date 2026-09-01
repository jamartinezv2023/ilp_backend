# Review data dictionary

ReviewRound is the frozen review-round identifier. ReviewerCode is pseudonymous and must not contain names or email addresses. InstrumentId, Version, ItemId, DimensionCode and ItemText are immutable source fields. The six criterion fields accept integers 1-4. Essentiality1to3 accepts 1-3. BiasRisk accepts None, Minor or Major. Recommendation accepts Retain, Revise or Remove. ProposedRevision and Rationale are qualitative text. CompletedUtc uses ISO 8601 UTC.

Completed reviewer files must be stored outside the public repository in an access-controlled research location. The blank templates may remain version controlled. The code-to-identity linkage, if one is required, is stored separately under the approved retention schedule.