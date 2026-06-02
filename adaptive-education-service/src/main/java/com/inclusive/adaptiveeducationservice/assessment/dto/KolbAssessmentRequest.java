package com.inclusive.adaptiveeducationservice.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record KolbAssessmentRequest(

        @NotBlank
        String studentId,

        @NotEmpty
        @Size(min = 48, max = 48)
        List<Integer> answers
) {
}