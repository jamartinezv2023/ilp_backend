package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record TeacherActionPlanResponse(

        String priorityLevel,

        String mainGoal,

        String recommendedStrategy,

        String duaPrinciple,

        String followUpAction,

        List<String> evidence,

        List<String> successCriteria
) {
}
