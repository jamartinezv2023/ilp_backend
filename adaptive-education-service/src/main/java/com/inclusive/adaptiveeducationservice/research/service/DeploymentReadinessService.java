package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DeploymentReadinessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeploymentReadinessService {

    public DeploymentReadinessResponse generateDeploymentReadinessPreview() {

        return new DeploymentReadinessResponse(
                "CONTAINERIZATION_REQUIRED",
                "DOCKERFILE_PENDING",
                "KUBERNETES_MANIFESTS_PENDING",
                "CLOUD_DEPLOYMENT_READY_AFTER_CONTAINERIZATION",
                "VALIDATED_WITH_POSTGRESQL_RUNTIME",
                List.of(
                        "Spring Boot service build successful",
                        "PostgreSQL runtime validated",
                        "REST endpoints validated locally",
                        "Gradle multi-module build available",
                        "Git traceability available",
                        "Quality compliance preview implemented"
                ),
                "Create Dockerfile, docker-compose profile, Kubernetes manifests and deployment documentation for reproducible backend evaluation."
        );
    }
}
