package com.inclusive.common.events;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Emitted by adaptive-education-service when a new learning path / recommendation
 * is produced for a student after processing inputs (assessments, profile signals, etc.).
 */
public record LearningPathRecommendedEvent(
        Long studentId,
        String tenantId,
        String recommendationId,
        String reason,
        List<String> recommendedItems,
        OffsetDateTime recommendedAt
) {}