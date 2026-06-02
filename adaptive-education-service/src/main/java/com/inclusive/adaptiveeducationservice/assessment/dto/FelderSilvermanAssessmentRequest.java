package com.inclusive.adaptiveeducationservice.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FelderSilvermanAssessmentRequest(

        @NotBlank
        String studentId,

        @NotEmpty
        @Size(min = 44, max = 44)
        List<String> answers
) {
}