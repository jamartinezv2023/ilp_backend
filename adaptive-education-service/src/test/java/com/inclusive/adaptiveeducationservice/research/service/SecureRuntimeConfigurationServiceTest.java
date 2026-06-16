package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecureRuntimeConfigurationServiceTest {

    private final SecureRuntimeConfigurationService service =
            new SecureRuntimeConfigurationService();

    @Test
    void shouldGenerateRuntimeConfigPreview() {
        var response = service.generateRuntimeConfigPreview();

        assertThat(response.runtimeConfigurationStatus()).contains("RUNTIME_CONFIGURATION");
        assertThat(response.secretManagementStatus()).contains("ENVIRONMENT_VARIABLES");
        assertThat(response.environmentSeparationStatus()).contains("DEV_TEST_PROD");
        assertThat(response.datasourceSecurityStatus()).contains("DATABASE_CREDENTIALS");
        assertThat(response.configurationEvidence()).isNotEmpty();
        assertThat(response.recommendedConfigurationAction()).contains("Spring profiles");
    }
}
