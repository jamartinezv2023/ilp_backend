package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record OpenApiContractComplianceResponse(

        String contractLanguage,

        String apiDocumentationStatus,

        String apiVersioningStatus,

        String hateoasReadiness,

        String contractTraceabilityStatus,

        List<String> documentedContractElements,

        String recommendedContractAction
) {
}
