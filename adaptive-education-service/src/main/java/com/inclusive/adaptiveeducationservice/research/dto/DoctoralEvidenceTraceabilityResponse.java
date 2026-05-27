package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DoctoralEvidenceTraceabilityResponse(

        String traceabilityLevel,

        String technicalEvidenceStatus,

        String methodologicalEvidenceStatus,

        String ethicalEvidenceStatus,

        List<String> traceableEvidenceSources,

        String recommendedDocumentationAction
) {
}
