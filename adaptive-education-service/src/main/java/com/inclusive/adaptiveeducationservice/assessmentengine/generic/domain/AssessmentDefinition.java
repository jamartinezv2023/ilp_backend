package com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record AssessmentDefinition(
        String id,
        String code,
        String name,
        String description,
        String version,
        String scoringStrategy,
        String interpretationStrategy,
        String instructions,
        boolean active,
        List<AssessmentQuestion> questions,
        Instant createdAt,
        Instant updatedAt
) {

    public AssessmentDefinition {
        Objects.requireNonNull(id, "Definition id is required");
        Objects.requireNonNull(code, "Definition code is required");
        Objects.requireNonNull(name, "Definition name is required");
        Objects.requireNonNull(version, "Definition version is required");

        questions = questions == null ? List.of() : List.copyOf(questions);

        if (id.isBlank()) {
            throw new IllegalArgumentException("Definition id cannot be blank");
        }

        if (code.isBlank()) {
            throw new IllegalArgumentException("Definition code cannot be blank");
        }

        if (version.isBlank()) {
            throw new IllegalArgumentException("Definition version cannot be blank");
        }
    }
}
