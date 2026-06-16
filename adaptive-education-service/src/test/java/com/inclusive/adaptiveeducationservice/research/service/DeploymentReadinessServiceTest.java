package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeploymentReadinessServiceTest {

    private final DeploymentReadinessService service = new DeploymentReadinessService();

    @Test
    void shouldGenerateDeploymentReadinessPreview() {
        var response = service.generateDeploymentReadinessPreview();

        assertThat(response.containerizationStatus()).isEqualTo("CONTAINERIZATION_REQUIRED");
        assertThat(response.dockerReadiness()).isEqualTo("DOCKERFILE_PENDING");
        assertThat(response.localRuntimeValidation()).isEqualTo("VALIDATED_WITH_POSTGRESQL_RUNTIME");
        assertThat(response.deploymentEvidence()).isNotEmpty();
        assertThat(response.recommendedDeploymentAction()).contains("Dockerfile");
    }
}
