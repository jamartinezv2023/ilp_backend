package com.inclusive.common.events.v1;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * V1 event contract.
 * Emitted by adaptive-education-service to request ML/DL inference from adaptive-intelligence-service.
 */
public record LearningPathInferenceRequestedEvent(
        UUID eventId,
        UUID correlationId,
        String schemaVersion,
        OffsetDateTime occurredAt,

        Long studentId,
        String tenantId,
        Long assessmentId,
        String assessmentType,
        Double score,
        String result,

        String policyName,
        String policyVersion,
        String candidateItemsCsv,
        String rationale,

        Map<String, String> metadata
) {
    public static final String V1 = "v1";

    public static LearningPathInferenceRequestedEvent of(
            UUID correlationId,
            Long studentId,
            String tenantId,
            Long assessmentId,
            String assessmentType,
            Double score,
            String result,
            String policyName,
            String policyVersion,
            String candidateItemsCsv,
            String rationale,
            Map<String, String> metadata
    ) {
        return new LearningPathInferenceRequestedEvent(
                UUID.randomUUID(),
                correlationId == null ? UUID.randomUUID() : correlationId,
                V1,
                OffsetDateTime.now(),
                studentId,
                tenantId,
                assessmentId,
                assessmentType,
                score,
                result,
                policyName,
                policyVersion,
                candidateItemsCsv,
                rationale,
                metadata
        );
    }
}