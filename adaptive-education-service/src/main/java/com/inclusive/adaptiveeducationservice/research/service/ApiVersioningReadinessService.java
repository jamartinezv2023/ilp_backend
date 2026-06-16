package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ApiVersioningReadinessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiVersioningReadinessService {

    public ApiVersioningReadinessResponse generateApiVersioningPreview() {

        return new ApiVersioningReadinessResponse(
                "VERSIONING_READY",
                "BASE_ENDPOINTS_AVAILABLE_WITH_VERSIONING_STRATEGY_PENDING",
                "HATEOAS_LINK_STRATEGY_PENDING_IMPLEMENTATION",
                "BACKWARD_COMPATIBILITY_POLICY_REQUIRED",
                List.of(
                        "REST controllers available",
                        "DTO response schemas defined",
                        "Quality endpoints exposed",
                        "OpenAPI contract readiness identified",
                        "Git traceability available"
                ),
                "Introduce /api/v1 namespace, document versioning policy and add HATEOAS self-links to evaluable backend resources."
        );
    }
}
