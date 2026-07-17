package com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain;

import java.util.List;
import java.util.Objects;

public record AssessmentQuestion(
        String id,
        String code,
        String text,
        String dimension,
        AssessmentQuestionType type,
        boolean required,
        int orderIndex,
        List<AssessmentOption> options
) {

    public AssessmentQuestion {
        Objects.requireNonNull(id, "Question id is required");
        Objects.requireNonNull(code, "Question code is required");
        Objects.requireNonNull(text, "Question text is required");
        Objects.requireNonNull(type, "Question type is required");

        options = options == null ? List.of() : List.copyOf(options);

        if (id.isBlank()) {
            throw new IllegalArgumentException("Question id cannot be blank");
        }

        if (code.isBlank()) {
            throw new IllegalArgumentException("Question code cannot be blank");
        }
    }
}
