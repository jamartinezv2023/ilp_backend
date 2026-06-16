package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DockerComposeDeploymentServiceTest {

    private final DockerComposeDeploymentService service =
            new DockerComposeDeploymentService();

    @Test
    void shouldGenerateDockerComposePreview() {
        var response = service.generateDockerComposePreview();

        assertThat(response.composeReadinessStatus()).contains("DOCKER_COMPOSE");
        assertThat(response.serviceOrchestrationStatus()).contains("BACKEND_AND_DATABASE");
        assertThat(response.databaseServiceStatus()).contains("POSTGRESQL");
        assertThat(response.environmentConfigurationStatus()).contains("ENVIRONMENT_VARIABLES");
        assertThat(response.composeEvidence()).isNotEmpty();
        assertThat(response.recommendedComposeAction()).contains("docker-compose.yml");
    }
}
