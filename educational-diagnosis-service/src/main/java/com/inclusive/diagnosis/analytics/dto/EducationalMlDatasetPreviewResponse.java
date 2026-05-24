package com.inclusive.diagnosis.analytics.dto;

public record EducationalMlDatasetPreviewResponse(

        String studentId,

        String kolbStyle,

        String supportDimension,

        double progressScore,

        boolean engagementImproved,

        String riskLevel,

        String recommendedSupportIntensity,

        String ethicalLabelPolicy
) {
}
