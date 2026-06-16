package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EthicsReadinessResponse(

        String ethicsReadiness,

        String humanOversight,

        String consentManagement,

        String dataProtectionStatus,

        String participantRiskLevel,

        String sensitiveDataHandling,

        String ethicsCommitteeSubmission,

        List<String> ethicsEvidence,

        String recommendedAction
) {
}
