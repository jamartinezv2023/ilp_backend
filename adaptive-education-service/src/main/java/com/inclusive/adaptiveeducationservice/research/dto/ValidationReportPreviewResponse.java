package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ValidationReportPreviewResponse(

        String validationReportStatus,

        String scientificReadiness,

        String methodologicalConsistency,

        String ethicalValidationStatus,

        List<String> validatedIntelligenceDimensions,

        String recommendedDoctoralAction
) {
}
