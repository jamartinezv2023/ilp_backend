package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DeploymentHealthCheckResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeploymentHealthCheckService {

    public DeploymentHealthCheckResponse generateHealthCheckPreview() {

        return new DeploymentHealthCheckResponse(
                "HEALTH_CHECK_GOVERNANCE_REQUIRED",
                "LIVENESS_AND_READINESS_PROBES_REQUIRED",
                "DOCKER_CONTAINER_RUNTIME_VALIDATED",
                "SPRING_BOOT_ACTUATOR_AVAILABLE",
                "KUBERNETES_PROBES_READY_AFTER_ACTUATOR_EXPOSURE",
                List.of(
                        "Docker Compose deployment validated",
                        "Backend container started successfully",
                        "PostgreSQL container started successfully",
                        "REST endpoint validated after container startup",
                        "Spring Boot actuator dependency available",
                        "Deployment readiness preview implemented"
                ),
                "Expose actuator health endpoint, add Docker Compose healthcheck and prepare Kubernetes liveness/readiness probes."
        );
    }
}
