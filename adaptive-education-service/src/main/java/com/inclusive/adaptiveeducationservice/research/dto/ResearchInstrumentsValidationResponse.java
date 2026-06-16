package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ResearchInstrumentsValidationResponse(

        String instrumentsValidationStatus,

        String teacherQuestionnaireStatus,

        String expertRubricStatus,

        String interviewGuideStatus,

        String observationProtocolStatus,

        String usabilityEvaluationStatus,

        List<String> validationDimensions,

        String recommendedAction
) {
}
