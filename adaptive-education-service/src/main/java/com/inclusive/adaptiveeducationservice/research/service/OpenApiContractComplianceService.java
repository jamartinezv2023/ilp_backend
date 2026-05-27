package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.OpenApiContractComplianceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenApiContractComplianceService {

    public OpenApiContractComplianceResponse generateOpenApiContractPreview() {

        return new OpenApiContractComplianceResponse(
                "OPENAPI_IDL_READY",
                "PENDING_FORMAL_SPRINGDOC_INTEGRATION",
                "API_VERSIONING_STRATEGY_REQUIRED",
                "HATEOAS_READINESS_PENDING",
                "TRACEABLE_BY_ENDPOINT_AND_GIT_HISTORY",
                List.of(
                        "REST endpoint path",
                        "HTTP method",
                        "Response DTO schema",
                        "Versioning requirement",
                        "Governance and quality evidence",
                        "Git commit traceability"
                ),
                "Integrate SpringDoc OpenAPI, define /v1 route strategy, document response schemas and prepare HATEOAS links for evaluable backend compliance."
        );
    }
}
