package com.inclusive.adaptiveeducationservice.intervention.dto;

import jakarta.validation.constraints.NotBlank;

public record EducationalInterventionRequest(
        @NotBlank String studentId,
        @NotBlank String title,
        @NotBlank String responsibleRole,
        @NotBlank String interventionType,
        @NotBlank String description
) {
}