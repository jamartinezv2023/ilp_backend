package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ApiLifecycleGovernanceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiLifecycleGovernanceService {

    public ApiLifecycleGovernanceResponse generateLifecyclePreview() {

        return new ApiLifecycleGovernanceResponse(
                "API_LIFECYCLE_GOVERNANCE_ACTIVE",
                "OPENAPI_AND_SWAGGER_PUBLICATION_AVAILABLE",
                "READY_FOR_CONTROLLED_EVOLUTION",
                "DEPRECATION_POLICY_REQUIRED_BEFORE_PUBLIC_RELEASE",
                "HIGH_TRACEABILITY_AND_AUDITABILITY",
                List.of(
                        "Swagger UI operational",
                        "OpenAPI runtime contract available",
                        "REST endpoints versioned under /api/v1",
                        "Analytics endpoints documented",
                        "Contract export enabled",
                        "Git commit traceability active",
                        "Architecture governance previews available"
                ),
                "Define API lifecycle policy including publication, semantic versioning, compatibility, deprecation and institutional review workflow."
        );
    }
}
