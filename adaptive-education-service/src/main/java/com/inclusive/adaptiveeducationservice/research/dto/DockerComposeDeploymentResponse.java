package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DockerComposeDeploymentResponse(

        String composeReadinessStatus,

        String serviceOrchestrationStatus,

        String databaseServiceStatus,

        String environmentConfigurationStatus,

        String reproducibleDeploymentStatus,

        List<String> composeEvidence,

        String recommendedComposeAction
) {
}
