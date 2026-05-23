package com.inclusive.diagnosis.analytics.dto;

public record PedagogicalPriorityPlanResponse(

        String priorityLevel,

        String recommendedAction,

        String teacherRecommendation,

        String evidenceSummary,

        double confidence
) {
}
