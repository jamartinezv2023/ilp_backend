package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record BiasFairnessAssessmentResponse(

        String fairnessStatus,

        String biasRiskLevel,

        List<String> protectedEducationalPrinciples,

        String mitigationStrategy,

        String humanReviewRequirement,

        String ethicalFairnessPolicy
) {
}
