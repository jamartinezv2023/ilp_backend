package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EthicsReadinessServiceTest {

    private final EthicsReadinessService service = new EthicsReadinessService();

    @Test
    void shouldGenerateEthicsReadinessPreview() {
        var response = service.generateEthicsReadinessPreview();

        assertThat(response.ethicsReadiness()).contains("ETHICS");
        assertThat(response.humanOversight()).isEqualTo("MANDATORY");
        assertThat(response.consentManagement()).contains("CONSENT");
        assertThat(response.participantRiskLevel()).contains("LOW");
        assertThat(response.ethicsEvidence()).isNotEmpty();
        assertThat(response.recommendedAction()).contains("informed consent");
    }
}
