package com.inclusive.adaptiveeducationservice.assessmenthistory.dto;

import java.time.Instant;

public record AssessmentTimelineItemResponse(
        String eventId,
        String studentId,
        String eventType,
        String title,
        String description,
        Instant occurredAt
) {
}