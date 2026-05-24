package com.inclusive.diagnosis.analytics.dto;

public record LearningEvolutionResponse(

        String studentProfileId,

        String evolutionTrend,

        double averageProgressScore,

        long interventionsCount,

        long improvedEngagementCount,

        String engagementTrend,

        String recommendedNextPhase
) {
}
