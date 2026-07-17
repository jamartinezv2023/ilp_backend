package com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record AssessmentAdministration(
        String id,
        String participantId,
        String assessmentCode,
        String assessmentVersion,
        AssessmentAdministrationStatus status,
        Instant startedAt,
        Instant completedAt,
        Long durationSeconds,
        String institutionId,
        String cohortId,
        String administratorId,
        Map<String, String> context
) {

    public AssessmentAdministration {
        Objects.requireNonNull(id, "Administration id is required");
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
        Objects.requireNonNull(status, "Administration status is required");

        context = context == null ? Map.of() : Map.copyOf(context);
    }
}
