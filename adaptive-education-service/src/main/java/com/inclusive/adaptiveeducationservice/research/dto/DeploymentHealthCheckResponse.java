package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DeploymentHealthCheckResponse(

        String healthCheckStatus,

        String livenessReadinessStatus,

        String containerMonitoringStatus,

        String actuatorReadiness,

        String kubernetesProbeReadiness,

        List<String> healthCheckEvidence,

        String recommendedHealthCheckAction
) {
}
