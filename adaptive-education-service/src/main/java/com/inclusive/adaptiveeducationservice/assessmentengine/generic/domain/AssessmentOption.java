package com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain;

import java.util.Objects;

public record AssessmentOption(
        String id,
        String code,
        String text,
        String dimension,
        Double numericValue,
        Double weight,
        int orderIndex
) {

    public AssessmentOption {
        Objects.requireNonNull(id, "Option id is required");
        Objects.requireNonNull(code, "Option code is required");
        Objects.requireNonNull(text, "Option text is required");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Option id cannot be blank");
        }

        if (code.isBlank()) {
            throw new IllegalArgumentException("Option code cannot be blank");
        }
    }
}
