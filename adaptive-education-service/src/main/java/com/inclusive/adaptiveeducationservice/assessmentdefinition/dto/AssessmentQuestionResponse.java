package com.inclusive.adaptiveeducationservice.assessmentdefinition.dto;

import java.util.List;

public record AssessmentQuestionResponse(
        String id,
        Integer questionNumber,
        String text,
        String dimension,
        String helpText,
        Boolean required,
        String questionType,
        Integer displayOrder,
        List<AssessmentOptionResponse> options
) {
}