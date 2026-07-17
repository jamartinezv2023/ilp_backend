package com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record AssessmentHistoryEntry(
        String historyId,
        String participantId,
        String administrationId,
        String assessmentCode,
        String assessmentVersion,
        AssessmentResult result,
        String institutionId,
        String cohortId,
        String grade,
        String course,
        String administratorId,
        Map<String, String> context,
        Instant recordedAt
) {

    public AssessmentHistoryEntry {
        Objects.requireNonNull(historyId, "History id is required");
        Objects.requireNonNull(
                participantId,
                "Participant id is required"
        );
        Objects.requireNonNull(
                administrationId,
                "Administration id is required"
        );
        Objects.requireNonNull(
                assessmentCode,
                "Assessment code is required"
        );
        Objects.requireNonNull(
                assessmentVersion,
                "Assessment version is required"
        );
        Objects.requireNonNull(result, "Assessment result is required");

        context = context == null ? Map.of() : Map.copyOf(context);

        recordedAt = recordedAt == null
                ? Instant.now()
                : recordedAt;
    }
}
