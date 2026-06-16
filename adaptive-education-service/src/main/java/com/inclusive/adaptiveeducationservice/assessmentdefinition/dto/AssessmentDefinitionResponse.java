package com.inclusive.adaptiveeducationservice.assessmentdefinition.dto;

import java.time.Instant;
import java.util.List;

public record AssessmentDefinitionResponse(
        String id,
        String code,
        String name,
        String description,
        String assessmentType,
        String version,
        Boolean active,
        Integer estimatedMinutes,
        String instructions,
        Instant createdAt,
        List<AssessmentQuestionResponse> questions
) {
}