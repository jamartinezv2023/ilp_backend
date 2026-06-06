package com.inclusive.adaptiveeducationservice.assessmentresponse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssessmentAnswerRequest(
        @NotBlank String questionId,
        @NotBlank String optionId,
        @NotBlank String dimension,
        @NotBlank String value,
        @NotNull Integer score
) {
}