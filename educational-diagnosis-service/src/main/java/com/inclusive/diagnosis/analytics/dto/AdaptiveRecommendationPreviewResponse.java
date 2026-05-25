package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record AdaptiveRecommendationPreviewResponse(

        String studentId,

        List<String> recommendedSequence,

        String predictedOutcome,

        double recommendationConfidence,

        String ethicalRecommendationPolicy
) {
}
