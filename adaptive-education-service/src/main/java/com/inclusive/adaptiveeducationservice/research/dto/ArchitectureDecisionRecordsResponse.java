package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ArchitectureDecisionRecordsResponse(

        String adrStatus,

        String decisionTraceabilityLevel,

        List<String> documentedDecisionAreas,

        String architecturalRationaleStatus,

        String technicalDebtControlStatus,

        String recommendedAdrAction
) {
}
