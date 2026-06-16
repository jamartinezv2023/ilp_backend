package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ApiSecurityGovernanceResponse(

        String apiSecurityGovernanceStatus,

        String authenticationReadiness,

        String authorizationReadiness,

        String contractSecurityStatus,

        String vulnerabilityReviewStatus,

        List<String> securityEvidence,

        String recommendedSecurityAction
) {
}
