package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KubernetesReadinessServiceTest {

    private final KubernetesReadinessService service =
            new KubernetesReadinessService();

    @Test
    void shouldGenerateKubernetesReadinessPreview() {
        var response = service.generateKubernetesReadinessPreview();

        assertThat(response.kubernetesReadinessStatus()).contains("KUBERNETES");
        assertThat(response.deploymentManifestStatus()).contains("DEPLOYMENT");
        assertThat(response.serviceExposureStatus()).contains("SERVICE");
        assertThat(response.livenessProbeStatus()).contains("LIVENESS");
        assertThat(response.readinessProbeStatus()).contains("READINESS");
        assertThat(response.kubernetesEvidence()).isNotEmpty();
        assertThat(response.recommendedKubernetesAction()).contains("ConfigMap");
    }
}
