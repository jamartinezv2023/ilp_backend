package com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record AssessmentSubmission(
        String administrationId,
        String participantId,
        String assessmentCode,
        String assessmentVersion,
        List<AssessmentResponse> responses,
        Map<String, String> context,
        Instant submittedAt
) {

    public AssessmentSubmission {
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

        responses = responses == null
                ? List.of()
                : List.copyOf(responses);

        context = context == null
                ? Map.of()
                : Map.copyOf(context);

        submittedAt = submittedAt == null
                ? Instant.now()
                : submittedAt;
    }
}
