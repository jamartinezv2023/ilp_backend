package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record EducationalRiskProfileResponse(

        String studentProfileId,

        String riskLevel,

        double riskScore,

        List<String> detectedFactors,

        String recommendedPreventiveAction
) {
}
