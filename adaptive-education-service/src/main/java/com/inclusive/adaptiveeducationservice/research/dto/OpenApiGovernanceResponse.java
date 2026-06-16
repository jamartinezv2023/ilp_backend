package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record OpenApiGovernanceResponse(

        String openApiStatus,

        String swaggerUiStatus,

        String apiDiscoverability,

        String contractGovernanceLevel,

        String versioningGovernance,

        List<String> contractEvidence,

        String recommendedOpenApiAction
) {
}
