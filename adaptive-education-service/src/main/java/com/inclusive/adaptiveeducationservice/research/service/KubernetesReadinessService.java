package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.KubernetesReadinessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KubernetesReadinessService {

    public KubernetesReadinessResponse generateKubernetesReadinessPreview() {

        return new KubernetesReadinessResponse(
                "KUBERNETES_READY_AFTER_MANIFESTS",
                "DEPLOYMENT_MANIFEST_REQUIRED",
                "SERVICE_MANIFEST_REQUIRED",
                "ACTUATOR_LIVENESS_PROBE_AVAILABLE",
                "ACTUATOR_READINESS_PROBE_AVAILABLE",
                List.of(
                        "Dockerfile created",
                        "Docker Compose deployment validated",
                        "Actuator health endpoint validated",
                        "Liveness and readiness groups exposed",
                        "PostgreSQL dependency containerized",
                        "Environment-based runtime configuration available"
                ),
                "Create Kubernetes Deployment, Service, ConfigMap and Secret manifests using actuator health probes for liveness and readiness."
        );
    }
}
