package com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record AssessmentResponse(
        String questionCode,
        List<String> selectedOptionIds,
        Map<String, Integer> rankings,
        Double numericValue,
        String textValue
) {

    public AssessmentResponse {
        Objects.requireNonNull(
                questionCode,
                "Question code is required"
        );

        selectedOptionIds = selectedOptionIds == null
                ? List.of()
                : List.copyOf(selectedOptionIds);

        rankings = rankings == null
                ? Map.of()
                : Map.copyOf(rankings);

        if (questionCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Question code cannot be blank"
            );
        }
    }

    public boolean hasValue() {
        return !selectedOptionIds.isEmpty()
                || !rankings.isEmpty()
                || numericValue != null
                || (textValue != null && !textValue.isBlank());
    }
}
