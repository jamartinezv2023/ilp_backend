package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecretsGovernanceServiceTest {

    private final SecretsGovernanceService service =
            new SecretsGovernanceService();

    @Test
    void shouldGenerateSecretsGovernancePreview() {
        var response = service.generateSecretsGovernancePreview();

        assertThat(response.secretsGovernanceStatus()).contains("SECRETS_GOVERNANCE");
        assertThat(response.credentialExposureRisk()).contains("CONTROLLED");
        assertThat(response.environmentVariablePolicy()).contains("ENVIRONMENT_VARIABLES");
        assertThat(response.ciCdSecretsReadiness()).contains("SECRETS");
        assertThat(response.secretsGovernanceEvidence()).isNotEmpty();
        assertThat(response.recommendedSecretsAction()).contains("secret scanning");
    }
}
