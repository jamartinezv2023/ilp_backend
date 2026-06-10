package com.inclusive.adaptiveeducationservice.mlpipeline.dto;

import java.time.Instant;
import java.util.Map;

public record KolbBaselineEvaluationResponse(
        String experimentId,
        Integer totalRows,
        Integer trainRows,
        Integer validationRows,
        Integer testRows,
        String baselineStrategy,
        String predictedMajorityClass,
        Double validationAccuracy,
        Double testAccuracy,
        Map<String, Long> classDistribution,
        String readinessStatus,
        Instant evaluatedAt
) {
}