package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ArchitectureDocumentationResponse(

        String documentationStatus,

        String diagramReadiness,

        String architectureViewModel,

        List<String> documentedArchitectureViews,

        String evaluatorReadiness,

        String recommendedDocumentationAction
) {
}
