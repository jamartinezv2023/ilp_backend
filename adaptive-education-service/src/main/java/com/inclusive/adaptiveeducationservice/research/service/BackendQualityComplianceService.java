package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.BackendQualityComplianceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackendQualityComplianceService {

    public BackendQualityComplianceResponse generateBackendCompliancePreview() {

        return new BackendQualityComplianceResponse(
                "HEXAGONAL_MODULAR_BACKEND_ARCHITECTURE",
                "API_CONTRACTS_PENDING_FORMAL_OPENAPI_DOCUMENTATION",
                "LOCAL_RUNTIME_VALIDATED_WITH_POSTGRESQL",
                "REST_ENDPOINTS_VERSIONING_AND_HATEOAS_PENDING_HARDENING",
                "DOCKER_AND_KUBERNETES_DEPLOYMENT_PENDING",
                "SONARCLOUD_AND_JACOCO_ENABLED_BUT_QUALITY_GATE_REQUIRES_FINAL_VALIDATION",
                List.of(
                        "Gradle build successful",
                        "PostgreSQL runtime validated",
                        "Functional REST endpoints validated",
                        "Git commit traceability active",
                        "Architecture tests enforced",
                        "SonarCloud integration started",
                        "Jacoco XML reporting enabled"
                ),
                "Prepare OpenAPI contracts, Docker deployment, Kubernetes readiness, HATEOAS strategy and SonarCloud quality gate evidence."
        );
    }
}
