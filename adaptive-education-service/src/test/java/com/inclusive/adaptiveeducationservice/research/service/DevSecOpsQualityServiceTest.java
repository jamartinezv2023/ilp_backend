package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DevSecOpsQualityServiceTest {

    private final DevSecOpsQualityService service = new DevSecOpsQualityService();

    @Test
    void shouldGenerateDevSecOpsQualityPreview() {
        var response = service.generateDevSecOpsPreview();

        assertThat(response.ciPipelineStatus()).isEqualTo("PARTIALLY_AUTOMATED");
        assertThat(response.staticAnalysisStatus()).isEqualTo("SPOTBUGS_AND_SONARCLOUD_ENABLED");
        assertThat(response.coverageReadiness()).isEqualTo("JACOCO_XML_REPORTING_ENABLED");
        assertThat(response.devSecOpsEvidence()).isNotEmpty();
        assertThat(response.recommendedQualityAction()).contains("SonarCloud");
    }
}
