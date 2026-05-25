package com.inclusive.diagnosis.analytics.dto;

public record TemporalEducationalSignalResponse(

        String timestamp,

        double progressScore,

        double engagementRisk,

        double supportIntensity
) {
}
