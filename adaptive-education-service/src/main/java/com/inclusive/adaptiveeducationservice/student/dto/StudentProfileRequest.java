package com.inclusive.adaptiveeducationservice.student.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record StudentProfileRequest(

        @NotBlank
        String fullName,

        @NotBlank
        String grade,

        @Min(5)
        Integer age,

        @NotBlank
        String learningProfile,

        @NotBlank
        String vocationalInterest,

        @NotBlank
        String supportLevel,

        @NotEmpty
        List<String> inclusiveStrategies,

        @NotEmpty
        List<String> pedagogicalRecommendations
) {
}