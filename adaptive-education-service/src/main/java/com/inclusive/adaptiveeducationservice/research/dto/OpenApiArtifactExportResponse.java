package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record OpenApiArtifactExportResponse(

        String artifactStatus,

        String openApiFormat,

        String exportReadiness,

        String versionControlStatus,

        List<String> artifactEvidence,

        String recommendedArtifactAction
) {
}
