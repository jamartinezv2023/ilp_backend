package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record InstitutionalGovernanceReportResponse(

        String institutionalStatus,

        List<String> institutionalReasoning,

        String recommendedGovernanceAction,

        String governancePriority,

        double institutionalRiskScore
) {
}
