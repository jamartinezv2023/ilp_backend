package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EducationalRiskManagementResponse(

        String riskManagementLevel,

        String ethicalRiskStatus,

        String pedagogicalRiskStatus,

        String institutionalRiskStatus,

        List<String> mitigationControls,

        String escalationProtocol
) {
}
