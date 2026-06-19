# Missing Data Plan

## Strategy

1. Identify missingness by variable and timepoint.
2. Classify missingness as MCAR, MAR or MNAR when possible.
3. Report missingness percentage per dataset.
4. Avoid deleting records before evaluating bias.
5. Use imputation only when justified.
6. Preserve raw dataset and create processed dataset separately.

## Candidate methods

- Complete case analysis
- Mean or median imputation
- Mode imputation
- Forward fill for longitudinal records
- Multiple imputation when scientifically justified
