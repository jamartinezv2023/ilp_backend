package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record InstitutionalPolicyRecommendationResponse(

        String institutionTrend,

        String recommendedInstitutionalAction,

        double riskPopulationPercentage,

        String prioritySupportArea,

        List<String> detectedPatterns
) {
}
