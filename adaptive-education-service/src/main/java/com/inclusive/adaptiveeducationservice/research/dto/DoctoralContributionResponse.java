package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DoctoralContributionResponse(

        String contributionLevel,

        String mainScientificContribution,

        List<String> contributionDimensions,

        String originalityStatus,

        String doctoralDefenseStrength,

        String recommendedThesisAction
) {
}
