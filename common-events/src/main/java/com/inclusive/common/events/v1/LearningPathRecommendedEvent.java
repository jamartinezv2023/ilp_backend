package com.inclusive.common.events.v1;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * V1 event contract.
 * Emitted by adaptive-intelligence-service after inference.
 * Consumed by notification-service (and potentially analytics-service).
 */
public record LearningPathRecommendedEvent(
        UUID eventId,
        UUID correlationId,
        String schemaVersion,
        OffsetDateTime occurredAt,

        Long studentId,
        String tenantId,
        String recommendationId,

        String reason,
        List<String> recommendedItems,
        Double confidence,

        String policyName,
        String modelName,
        String modelVersion,

        Map<String, String> metadata
) {
    public static final String V1 = "v1";

    public static LearningPathRecommendedEvent of(
            UUID correlationId,
            Long studentId,
            String tenantId,
            String recommendationId,
            String reason,
            List<String> recommendedItems,
            Double confidence,
            String policyName,
            String modelName,
            String modelVersion,
            Map<String, String> metadata
    ) {
        return new LearningPathRecommendedEvent(
                UUID.randomUUID(),
                correlationId == null ? UUID.randomUUID() : correlationId,
                V1,
                OffsetDateTime.now(),
                studentId,
                tenantId,
                recommendationId,
                reason,
                recommendedItems,
                confidence == null ? 0.0 : confidence,
                policyName,
                modelName,
                modelVersion,
                metadata
        );
    }
}