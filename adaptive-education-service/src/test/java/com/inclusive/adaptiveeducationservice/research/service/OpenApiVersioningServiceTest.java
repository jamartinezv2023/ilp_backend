package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiVersioningServiceTest {

    private final OpenApiVersioningService service =
            new OpenApiVersioningService();

    @Test
    void shouldGenerateOpenApiVersioningPreview() {
        var response = service.generateVersioningPreview();

        assertThat(response.contractVersioningStatus()).contains("VERSIONING");
        assertThat(response.currentContractVersion()).contains("OPENAPI");
        assertThat(response.backwardCompatibilityPolicy()).contains("COMPATIBILITY");
        assertThat(response.breakingChangeControl()).contains("BREAKING_CHANGES");
        assertThat(response.versioningEvidence()).isNotEmpty();
        assertThat(response.recommendedVersioningAction()).contains("semantic");
    }
}
