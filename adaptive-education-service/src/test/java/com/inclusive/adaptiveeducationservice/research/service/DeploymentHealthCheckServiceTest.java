package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeploymentHealthCheckServiceTest {

    private final DeploymentHealthCheckService service =
            new DeploymentHealthCheckService();

    @Test
    void shouldGenerateHealthCheckPreview() {
        var response = service.generateHealthCheckPreview();

        assertThat(response.healthCheckStatus()).contains("HEALTH_CHECK");
        assertThat(response.livenessReadinessStatus()).contains("LIVENESS");
        assertThat(response.containerMonitoringStatus()).contains("DOCKER_CONTAINER");
        assertThat(response.actuatorReadiness()).contains("ACTUATOR");
        assertThat(response.healthCheckEvidence()).isNotEmpty();
        assertThat(response.recommendedHealthCheckAction()).contains("health endpoint");
    }
}
