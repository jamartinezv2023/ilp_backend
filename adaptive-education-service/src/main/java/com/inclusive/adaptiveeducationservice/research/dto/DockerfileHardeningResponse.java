package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DockerfileHardeningResponse(

        String dockerfileStatus,

        String baseImageStrategy,

        String runtimeSecurityStatus,

        String environmentVariableReadiness,

        String buildArtifactStrategy,

        List<String> dockerfileEvidence,

        String recommendedDockerAction
) {
}
