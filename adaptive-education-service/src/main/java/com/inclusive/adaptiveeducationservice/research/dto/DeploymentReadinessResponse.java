package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DeploymentReadinessResponse(

        String containerizationStatus,

        String dockerReadiness,

        String kubernetesReadiness,

        String cloudDeploymentReadiness,

        String localRuntimeValidation,

        List<String> deploymentEvidence,

        String recommendedDeploymentAction
) {
}
