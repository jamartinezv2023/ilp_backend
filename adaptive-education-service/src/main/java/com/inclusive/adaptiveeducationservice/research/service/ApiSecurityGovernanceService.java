package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ApiSecurityGovernanceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiSecurityGovernanceService {

    public ApiSecurityGovernanceResponse generateSecurityGovernancePreview() {

        return new ApiSecurityGovernanceResponse(
                "API_SECURITY_GOVERNANCE_REQUIRED",
                "JWT_AND_OAUTH2_INTEGRATION_PENDING",
                "ROLE_BASED_ACCESS_CONTROL_REQUIRED",
                "OPENAPI_SECURITY_SCHEMES_PENDING",
                "DEPENDENCY_AND_ENDPOINT_SECURITY_REVIEW_PENDING",
                List.of(
                        "OpenAPI contract available",
                        "Swagger UI operational",
                        "REST endpoints discoverable",
                        "Quality gate readiness available",
                        "DevSecOps preview implemented",
                        "Human-centered and governance AI policies documented"
                ),
                "Add OpenAPI security schemes, enforce JWT/OAuth2 integration, document RBAC rules and include dependency vulnerability scanning in CI/CD."
        );
    }
}
