package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EducationalEvaluationMetricsResponse(

        String evaluationReadiness,

        List<String> quantitativeMetrics,

        List<String> qualitativeMetrics,

        List<String> ethicalEvaluationCriteria,

        String teacherFeedbackRequirement,

        String recommendedEvaluationPhase
) {
}
