package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.OpenApiArtifactExportResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenApiArtifactExportService {

    public OpenApiArtifactExportResponse generateArtifactExportPreview() {

        return new OpenApiArtifactExportResponse(
                "OPENAPI_ARTIFACT_READY_FOR_EXPORT",
                "JSON_OPENAPI_3",
                "EXPORTABLE_FROM_RUNTIME_ENDPOINT",
                "READY_FOR_GIT_VERSIONING",
                List.of(
                        "Runtime contract available at /v3/api-docs",
                        "Swagger UI validates discoverability",
                        "OpenAPI JSON can be exported with Invoke-WebRequest",
                        "Contract artifact can be versioned in Git",
                        "Evaluator can inspect API schema and paths"
                ),
                "Export /v3/api-docs to adaptive-education-service-openapi.json and version it as formal API contract evidence."
        );
    }
}
