package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureDocumentationServiceTest {

    private final ArchitectureDocumentationService service =
            new ArchitectureDocumentationService();

    @Test
    void shouldGenerateArchitectureDocumentationPreview() {
        var response = service.generateDocumentationPreview();

        assertThat(response.documentationStatus()).contains("DOCUMENTATION");
        assertThat(response.diagramReadiness()).contains("DIAGRAMS");
        assertThat(response.architectureViewModel()).isEqualTo("C4_MODEL_RECOMMENDED");
        assertThat(response.documentedArchitectureViews()).isNotEmpty();
        assertThat(response.evaluatorReadiness()).contains("ARCHITECTURE_REVIEW");
        assertThat(response.recommendedDocumentationAction()).contains("C4 diagrams");
    }
}
