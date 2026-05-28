package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureRemediationRoadmapServiceTest {

    private final ArchitectureRemediationRoadmapService service =
            new ArchitectureRemediationRoadmapService();

    @Test
    void shouldGenerateArchitectureRemediationRoadmapPreview() {
        var response = service.generateRemediationRoadmapPreview();

        assertThat(response.roadmapStatus()).contains("ENGINEERING_HARDENING");
        assertThat(response.priorityLevel()).isEqualTo("HIGH_PRIORITY");
        assertThat(response.criticalRemediationItems()).isNotEmpty();
        assertThat(response.mediumTermHardeningItems()).isNotEmpty();
        assertThat(response.evidenceToProduce()).isNotEmpty();
        assertThat(response.recommendedNextEngineeringSprint()).contains("OpenAPI");
    }
}
