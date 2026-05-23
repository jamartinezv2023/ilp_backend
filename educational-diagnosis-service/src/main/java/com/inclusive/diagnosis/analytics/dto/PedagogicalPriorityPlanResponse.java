package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record PedagogicalPriorityPlanResponse(

        String priorityLevel,

        String recommendedAction,

        String teacherRecommendation,

        String evidenceSummary,

        double confidence,

        List<String> reasoning
) {
}
