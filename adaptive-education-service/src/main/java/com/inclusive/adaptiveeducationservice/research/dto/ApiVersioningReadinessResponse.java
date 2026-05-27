package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ApiVersioningReadinessResponse(

        String apiVersioningLevel,

        String currentVersioningStatus,

        String hateoasStrategyStatus,

        String backwardCompatibilityStatus,

        List<String> versioningEvidence,

        String recommendedApiEvolutionAction
) {
}
