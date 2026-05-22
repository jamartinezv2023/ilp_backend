package com.inclusive.diagnosis.questionnaire.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateDiagnosticQuestionnaireRequest(

        UUID tenantId,

        @NotBlank
        String title,

        @NotBlank
        String questionnaireType,

        @NotBlank
        String targetPopulation,

        Boolean active,

        String description,

        String educationalLevel,

        String accessibilityConsiderations,

        String dueAlignmentNotes
) {
}
