package com.inclusive.common.events.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonEventsJsonContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void shouldSerializeAndDeserializeAssessmentCompletedEvent() throws Exception {
        AssessmentCompletedEvent event = AssessmentCompletedEvent.of(
                UUID.randomUUID(),
                1L,
                2L,
                "tenant-a",
                "KOLB",
                90.0,
                "PASSED",
                OffsetDateTime.now()
        );

        String json = objectMapper.writeValueAsString(event);

        assertTrue(json.contains("\"schemaVersion\":\"v1\""));
        assertTrue(json.contains("\"tenantId\":\"tenant-a\""));

        AssessmentCompletedEvent restored =
                objectMapper.readValue(json, AssessmentCompletedEvent.class);

        assertEquals(event.schemaVersion(), restored.schemaVersion());
        assertEquals(event.assessmentId(), restored.assessmentId());
        assertEquals(event.studentId(), restored.studentId());
        assertEquals(event.tenantId(), restored.tenantId());
    }

    @Test
    void shouldSerializeAndDeserializeLearningPathRecommendedEvent() throws Exception {
        LearningPathRecommendedEvent event = LearningPathRecommendedEvent.of(
                UUID.randomUUID(),
                10L,
                "tenant-b",
                "REC-001",
                "high-score",
                List.of("module-1", "module-2"),
                0.85,
                "policy-a",
                "model-x",
                "1.0.0",
                Map.of("source", "contract-test")
        );

        String json = objectMapper.writeValueAsString(event);

        assertTrue(json.contains("\"schemaVersion\":\"v1\""));
        assertTrue(json.contains("\"recommendationId\":\"REC-001\""));
        assertTrue(json.contains("\"module-1\""));

        LearningPathRecommendedEvent restored =
                objectMapper.readValue(json, LearningPathRecommendedEvent.class);

        assertEquals(event.recommendationId(), restored.recommendationId());
        assertEquals(event.recommendedItems(), restored.recommendedItems());
        assertEquals(event.metadata(), restored.metadata());
        assertEquals(event.confidence(), restored.confidence());
    }
}
