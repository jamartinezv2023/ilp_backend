package com.inclusive.adaptiveeducationservice.intervention.dto;

import java.time.Instant;

public record EducationalInterventionResponse(
        String id,
        String studentId,
        String title,
        String responsibleRole,
        String interventionType,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}