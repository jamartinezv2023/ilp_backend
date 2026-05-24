package com.inclusive.diagnosis.analytics.dto;

public record EducationalFeatureStorePreviewResponse(

        String studentId,

        double kolbReflectiveScore,

        double engagementRiskScore,

        double supportIntensityScore,

        double interventionEffectivenessScore,

        double longitudinalImprovementScore,

        double adaptiveComplexityScore,

        String ethicalFeaturePolicy
) {
}
