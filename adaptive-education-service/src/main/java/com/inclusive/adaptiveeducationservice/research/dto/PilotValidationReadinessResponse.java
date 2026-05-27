package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record PilotValidationReadinessResponse(

        String pilotReadinessLevel,

        String datasetReadiness,

        String teacherValidationReadiness,

        String ethicalApprovalRequirement,

        List<String> pilotValidationCriteria,

        String recommendedPilotAction
) {
}
