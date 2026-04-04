package com.inclusive.common.events.v1;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * V1 event contract.
 * Emitted by assessment-service when an assessment is completed.
 */
public record AssessmentCompletedEvent(
        UUID eventId,
        UUID correlationId,
        String schemaVersion,
        OffsetDateTime occurredAt,

        Long assessmentId,
        Long studentId,
        String tenantId,
        String type,
        Double score,
        String result,
        OffsetDateTime completedAt
) {
    public static final String V1 = "v1";

    public static AssessmentCompletedEvent of(
            UUID correlationId,
            Long assessmentId,
            Long studentId,
            String tenantId,
            String type,
            Double score,
            String result,
            OffsetDateTime completedAt
    ) {
        return new AssessmentCompletedEvent(
                UUID.randomUUID(),
                correlationId == null ? UUID.randomUUID() : correlationId,
                V1,
                OffsetDateTime.now(),
                assessmentId,
                studentId,
                tenantId,
                type,
                score,
                result,
                completedAt
        );
    }
}