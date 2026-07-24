package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record AssessmentScientificObservation(
        String administrationId,
        String participantId,
        String assessmentCode,
        String assessmentVersion,
        String primaryProfile,
        String scoringAlgorithmVersion,
        String interpretationVersion,
        Instant calculatedAt,
        Instant submittedAt,
        Instant featureCutoffAt,
        List<ScientificScoreItem> scores,
        List<ScientificInterpretation> interpretations,
        ScientificSubmissionContext context
) {

    public AssessmentScientificObservation {
        Objects.requireNonNull(
                administrationId,
                "administrationId is required"
        );

        Objects.requireNonNull(
                participantId,
                "participantId is required"
        );

        Objects.requireNonNull(
                assessmentCode,
                "assessmentCode is required"
        );

        Objects.requireNonNull(
                assessmentVersion,
                "assessmentVersion is required"
        );

        scores = scores == null
                ? List.of()
                : List.copyOf(scores);

        interpretations = interpretations == null
                ? List.of()
                : List.copyOf(interpretations);
    }
}