package com.inclusive.adaptiveeducationservice.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record KuderAssessmentRequest(

        @NotBlank
        String studentId,

        @NotEmpty
        @Size(min = 30, max = 30)
        List<String> answers
) {
}