package com.inclusive.adaptiveeducationservice.mlmodels.dto;

import java.time.Instant;
import java.util.Map;

public record RandomForestEvaluationResponse(
        String experimentId,
        String algorithm,
        String target,
        Integer totalRows,
        Integer trainRows,
        Integer validationRows,
        Integer testRows,
        Double accuracy,
        Double precision,
        Double recall,
        Double f1Score,
        Map<String, Double> featureImportance,
        Map<String, Long> classDistribution,
        String readinessStatus,
        Instant createdAt
) {
}