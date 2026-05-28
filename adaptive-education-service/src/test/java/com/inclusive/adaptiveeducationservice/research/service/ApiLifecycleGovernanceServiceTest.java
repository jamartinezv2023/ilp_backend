package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiLifecycleGovernanceServiceTest {

    private final ApiLifecycleGovernanceService service =
            new ApiLifecycleGovernanceService();

    @Test
    void shouldGenerateApiLifecycleGovernancePreview() {
        var response = service.generateLifecyclePreview();

        assertThat(response.lifecycleGovernanceStatus())
                .contains("LIFECYCLE_GOVERNANCE");

        assertThat(response.apiPublicationStatus())
                .contains("OPENAPI");

        assertThat(response.apiEvolutionReadiness())
                .contains("CONTROLLED_EVOLUTION");

        assertThat(response.deprecationGovernance())
                .contains("DEPRECATION_POLICY");

        assertThat(response.auditabilityLevel())
                .contains("TRACEABILITY");

        assertThat(response.lifecycleEvidence())
                .isNotEmpty();

        assertThat(response.recommendedLifecycleAction())
                .contains("semantic versioning");
    }
}
