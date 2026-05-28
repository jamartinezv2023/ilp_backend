package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiArtifactExportServiceTest {

    private final OpenApiArtifactExportService service =
            new OpenApiArtifactExportService();

    @Test
    void shouldGenerateOpenApiArtifactExportPreview() {
        var response = service.generateArtifactExportPreview();

        assertThat(response.artifactStatus()).contains("OPENAPI_ARTIFACT");
        assertThat(response.openApiFormat()).isEqualTo("JSON_OPENAPI_3");
        assertThat(response.exportReadiness()).contains("EXPORTABLE");
        assertThat(response.versionControlStatus()).contains("GIT");
        assertThat(response.artifactEvidence()).isNotEmpty();
        assertThat(response.recommendedArtifactAction()).contains("adaptive-education-service-openapi.json");
    }
}
