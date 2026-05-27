package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EnterpriseArchitectureGovernanceResponse(

        String architectureGovernanceLevel,

        String architecturalStyle,

        String boundedContextStrategy,

        String layerSeparationStatus,

        String architectureEnforcementStatus,

        String enterpriseReadiness,

        List<String> governanceEvidence,

        String recommendedArchitectureAction
) {
}
