package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CiCdPipelineReadinessServiceTest {

    private final CiCdPipelineReadinessService service = new CiCdPipelineReadinessService();

    @Test
    void shouldGenerateCiCdPipelineReadinessPreview() {
        var response = service.generateCiCdPreview();

        assertThat(response.pipelineReadinessLevel()).isEqualTo("PIPELINE_READY_FOR_AUTOMATION");
        assertThat(response.buildAutomationStatus()).isEqualTo("GRADLE_BUILD_VALIDATED");
        assertThat(response.testAutomationStatus()).contains("TESTS_ENABLED");
        assertThat(response.qualityGateAutomationStatus()).contains("SONARCLOUD");
        assertThat(response.pipelineStages()).isNotEmpty();
        assertThat(response.recommendedPipelineAction()).contains("Azure DevOps");
    }
}
