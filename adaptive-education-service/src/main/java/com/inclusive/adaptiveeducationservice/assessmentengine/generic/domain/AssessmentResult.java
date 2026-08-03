package com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record AssessmentResult(
        String administrationId,
        String participantId,
        String assessmentCode,
        String assessmentVersion,
        String primaryProfile,
        Map<String, Double> scores,
        Map<String, String> interpretations,
        List<String> recommendations,
        String scoringAlgorithmVersion,
        Instant calculatedAt
) {

    public AssessmentResult {
        Objects.requireNonNull(
                administrationId,
                "Administration id is required"
        );
        Objects.requireNonNull(
                participantId,
                "Participant id is required"
        );
        Objects.requireNonNull(
                assessmentCode,
                "Assessment code is required"
        );
        Objects.requireNonNull(
                assessmentVersion,
                "Assessment version is required"
        );
        Objects.requireNonNull(
                scoringAlgorithmVersion,
                "Scoring algorithm version is required"
        );

        scores = scores == null ? Map.of() : Map.copyOf(scores);

        interpretations = interpretations == null
                ? Map.of()
                : Map.copyOf(interpretations);

        recommendations = recommendations == null
                ? List.of()
                : List.copyOf(recommendations);

        calculatedAt = calculatedAt == null
                ? Instant.now()
                : calculatedAt;
    }
}
