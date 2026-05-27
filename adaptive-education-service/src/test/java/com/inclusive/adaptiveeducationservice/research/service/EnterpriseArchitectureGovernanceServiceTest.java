package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnterpriseArchitectureGovernanceServiceTest {

    private final EnterpriseArchitectureGovernanceService service =
            new EnterpriseArchitectureGovernanceService();

    @Test
    void shouldGenerateEnterpriseArchitectureGovernancePreview() {
        var response = service.generateGovernancePreview();

        assertThat(response.architectureGovernanceLevel())
                .isEqualTo("ACTIVE_ARCHITECTURE_GOVERNANCE");
        assertThat(response.architecturalStyle())
                .isEqualTo("HEXAGONAL_MODULAR_ARCHITECTURE");
        assertThat(response.layerSeparationStatus())
                .contains("API_APPLICATION_DOMAIN_INFRASTRUCTURE");
        assertThat(response.architectureEnforcementStatus())
                .contains("ARCHITECTURE_TESTS");
        assertThat(response.governanceEvidence()).isNotEmpty();
        assertThat(response.recommendedArchitectureAction())
                .contains("bounded context");
    }
}
