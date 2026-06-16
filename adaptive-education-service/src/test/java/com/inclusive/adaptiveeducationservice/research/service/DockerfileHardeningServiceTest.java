package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DockerfileHardeningServiceTest {

    private final DockerfileHardeningService service =
            new DockerfileHardeningService();

    @Test
    void shouldGenerateDockerfilePreview() {
        var response = service.generateDockerfilePreview();

        assertThat(response.dockerfileStatus()).contains("DOCKERFILE");
        assertThat(response.baseImageStrategy()).contains("JAVA_17");
        assertThat(response.runtimeSecurityStatus()).contains("NON_ROOT");
        assertThat(response.environmentVariableReadiness()).contains("ENVIRONMENT_VARIABLES");
        assertThat(response.dockerfileEvidence()).isNotEmpty();
        assertThat(response.recommendedDockerAction()).contains("Dockerfile");
    }
}
