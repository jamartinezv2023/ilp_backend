package com.inclusive.adaptiveeducationservice.assessmentresponse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssessmentResponseRequest(
        @NotBlank String studentId,
        @NotBlank String assessmentCode,
        @NotBlank String assessmentVersion,
        @NotEmpty List<@Valid AssessmentAnswerRequest> answers
) {
}