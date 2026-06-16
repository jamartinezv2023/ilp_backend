package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record KubernetesReadinessResponse(

        String kubernetesReadinessStatus,

        String deploymentManifestStatus,

        String serviceExposureStatus,

        String livenessProbeStatus,

        String readinessProbeStatus,

        List<String> kubernetesEvidence,

        String recommendedKubernetesAction
) {
}
