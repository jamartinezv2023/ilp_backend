package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureDecisionRecordsServiceTest {

    private final ArchitectureDecisionRecordsService service =
            new ArchitectureDecisionRecordsService();

    @Test
    void shouldGenerateAdrPreview() {
        var response = service.generateAdrPreview();

        assertThat(response.adrStatus()).isEqualTo("ADR_DOCUMENTATION_REQUIRED");
        assertThat(response.decisionTraceabilityLevel()).contains("GIT");
        assertThat(response.documentedDecisionAreas()).isNotEmpty();
        assertThat(response.architecturalRationaleStatus()).contains("RATIONALE");
        assertThat(response.technicalDebtControlStatus()).contains("CONTROLLED");
        assertThat(response.recommendedAdrAction()).contains("/docs/adr");
    }
}
