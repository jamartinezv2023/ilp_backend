package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ValidationPreviewResponse(

        String validationStage,

        List<String> evaluatedComponents,

        List<String> evaluationCriteria,

        String researchReadiness,

        String recommendedNextStep
) {
}
