package com.inclusive.diagnosis.analytics.dto;

public record PolicyLearningPreviewResponse(

        String studentId,

        String currentPolicy,

        double policyPerformanceScore,

        String recommendedPolicyAdjustment,

        String adaptiveLearningSignal,

        double policyConfidence,

        String ethicalPolicyLearningStatement
) {
}
