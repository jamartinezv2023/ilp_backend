package com.inclusive.adaptiveeducationservice.assessmentdefinition.dto;

public record AssessmentOptionResponse(
        String id,
        String label,
        String value,
        Integer weight,
        Integer displayOrder
) {
}