package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.OpenApiVersioningResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenApiVersioningService {

    public OpenApiVersioningResponse generateVersioningPreview() {

        return new OpenApiVersioningResponse(
                "CONTRACT_VERSIONING_READY",
                "v0_RUNTIME_GENERATED_OPENAPI",
                "BACKWARD_COMPATIBILITY_POLICY_REQUIRED",
                "BREAKING_CHANGES_MUST_BE_REVIEWED_BEFORE_RELEASE",
                List.of(
                        "OpenAPI runtime contract available",
                        "Swagger UI operational",
                        "API v1 routes documented",
                        "Contract artifact export enabled",
                        "Git version control available"
                ),
                "Define semantic API contract versioning, store exported OpenAPI JSON per release and document breaking-change review rules."
        );
    }
}
