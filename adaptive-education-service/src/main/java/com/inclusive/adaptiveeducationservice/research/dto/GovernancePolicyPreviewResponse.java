package com.inclusive.adaptiveeducationservice.research.dto;

public record GovernancePolicyPreviewResponse(

        String governanceLevel,

        String ethicalPolicy,

        String traceabilityStatus,

        String decisionAuditability,

        String humanOversight,

        String institutionalRiskLevel
) {
}
