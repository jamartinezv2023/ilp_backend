package com.inclusive.adaptiveeducationservice.researchanalytics.dto;

import java.util.Map;

public record ResearchSummaryResponse(
        Integer totalFeatureRows,
        Integer uniqueStudents,
        Map<String, Long> kolbStyles,
        String datasetReadinessStatus,
        String recommendedNextAction
) {
}