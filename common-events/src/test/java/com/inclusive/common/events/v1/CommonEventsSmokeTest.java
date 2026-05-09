package com.inclusive.common.events.v1;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommonEventsSmokeTest {

    @Test
    void shouldCreateAssessmentCompletedEvent() {

        OffsetDateTime completedAt = OffsetDateTime.now();

        AssessmentCompletedEvent event =
                AssessmentCompletedEvent.of(
                        null,
                        100L,
                        200L,
                        "tenant-a",
                        "KOLB",
                        95.5,
                        "PASSED",
                        completedAt
                );

        assertNotNull(event.eventId());
        assertNotNull(event.correlationId());

        assertEquals("v1", event.schemaVersion());

        assertEquals(100L, event.assessmentId());
        assertEquals(200L, event.studentId());

        assertEquals("tenant-a", event.tenantId());
        assertEquals("KOLB", event.type());

        assertEquals(95.5, event.score());
        assertEquals("PASSED", event.result());

        assertEquals(completedAt, event.completedAt());

        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldCreateInferenceRequestedEvent() {

        Map<String, String> metadata =
                Map.of("source", "assessment-service");

        LearningPathInferenceRequestedEvent event =
                LearningPathInferenceRequestedEvent.of(
                        null,
                        10L,
                        "tenant-b",
                        20L,
                        "FELDER",
                        88.0,
                        "READY",
                        "policy-a",
                        "1.0.0",
                        "A,B,C",
                        "adaptive-rule",
                        metadata
                );

        assertNotNull(event.eventId());
        assertNotNull(event.correlationId());

        assertEquals("v1", event.schemaVersion());

        assertEquals(10L, event.studentId());
        assertEquals("tenant-b", event.tenantId());

        assertEquals(20L, event.assessmentId());

        assertEquals("FELDER", event.assessmentType());

        assertEquals(88.0, event.score());

        assertEquals("READY", event.result());

        assertEquals("policy-a", event.policyName());

        assertEquals("1.0.0", event.policyVersion());

        assertEquals("A,B,C", event.candidateItemsCsv());

        assertEquals("adaptive-rule", event.rationale());

        assertEquals(metadata, event.metadata());

        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldCreateRecommendedEventWithDefaultConfidence() {

        Map<String, String> metadata =
                Map.of("engine", "ml-v2");

        LearningPathRecommendedEvent event =
                LearningPathRecommendedEvent.of(
                        UUID.randomUUID(),
                        300L,
                        "tenant-c",
                        "REC-001",
                        "high-score",
                        List.of("module-1", "module-2"),
                        null,
                        "policy-b",
                        "model-x",
                        "2.1.0",
                        metadata
                );

        assertNotNull(event.eventId());

        assertEquals("v1", event.schemaVersion());

        assertEquals(300L, event.studentId());

        assertEquals("tenant-c", event.tenantId());

        assertEquals("REC-001", event.recommendationId());

        assertEquals("high-score", event.reason());

        assertEquals(
                List.of("module-1", "module-2"),
                event.recommendedItems()
        );

        assertEquals(0.0, event.confidence());

        assertEquals("policy-b", event.policyName());

        assertEquals("model-x", event.modelName());

        assertEquals("2.1.0", event.modelVersion());

        assertEquals(metadata, event.metadata());

        assertNotNull(event.occurredAt());
    }
}
