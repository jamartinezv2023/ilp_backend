package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record InstitutionalPolicyInsightResponse(

        String institutionTrend,

        String recommendedInstitutionalAction,

        double riskPopulationPercentage,

        String prioritySupportArea,

        List<String> detectedPatterns
) {
}
