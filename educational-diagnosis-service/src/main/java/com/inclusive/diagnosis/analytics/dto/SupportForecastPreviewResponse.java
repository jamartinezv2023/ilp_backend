package com.inclusive.diagnosis.analytics.dto;

public record SupportForecastPreviewResponse(

        String studentId,

        String predictedEngagementTrend,

        String predictedSupportIntensity,

        double predictedInterventionEffectiveness,

        double forecastConfidence,

        String recommendedNextSupportPhase,

        String ethicalForecastPolicy
) {
}
