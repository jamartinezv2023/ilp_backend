package com.inclusive.diagnosis.analytics.dto;

public record LearningProfileSummaryResponse(

        String dominantProfile,

        long reflectiveScore,

        long activeScore,

        long abstractScore,

        long concreteScore,

        double confidence
) {
}
