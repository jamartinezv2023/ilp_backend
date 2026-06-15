package com.inclusive.adaptiveeducationservice.mlmodels.dto;

import java.time.Instant;

public record MlExperimentResponse(
        String id,
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
        String featureImportanceJson,
        Instant createdAt
) {
}