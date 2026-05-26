package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EducationalTransparencyResponse(

        String transparencyLevel,

        String aiDisclosureStatus,

        List<String> disclosedDecisionElements,

        String teacherExplanationAvailability,

        String institutionalDisclosurePolicy,

        String transparencyRiskStatus
) {
}
