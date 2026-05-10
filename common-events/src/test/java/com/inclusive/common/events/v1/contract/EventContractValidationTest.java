package com.inclusive.common.events.v1.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;
import com.inclusive.common.events.v1.LearningPathInferenceRequestedEvent;
import com.inclusive.common.events.v1.LearningPathRecommendedEvent;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.inclusive.common.events.testing.contracts.EventContractValidator.assertMatchesContract;

class EventContractValidationTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void assessmentCompletedEventShouldMatchContract() throws Exception {
        AssessmentCompletedEvent event = AssessmentCompletedEvent.of(
                UUID.randomUUID(),
                1L,
                2L,
                "tenant-a",
                "KOLB",
                95.0,
                "PASSED",
                OffsetDateTime.now()
        );

        assertMatchesContract(objectMapper, event, "assessment-completed-event.json");
    }

    @Test
    void learningPathInferenceRequestedEventShouldMatchContract()
            throws Exception {
        LearningPathInferenceRequestedEvent event =
                LearningPathInferenceRequestedEvent.of(
                        UUID.randomUUID(),
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
                        Map.of("source", "contract-test")
                );

        assertMatchesContract(
                objectMapper,
                event,
                "learning-path-inference-requested-event.json"
        );
    }

    @Test
    void learningPathRecommendedEventShouldMatchContract()
            throws Exception {
        LearningPathRecommendedEvent event =
                LearningPathRecommendedEvent.of(
                        UUID.randomUUID(),
                        30L,
                        "tenant-c",
                        "REC-001",
                        "high-score",
                        List.of("module-1", "module-2"),
                        0.85,
                        "policy-b",
                        "model-x",
                        "2.1.0",
                        Map.of("engine", "contract-test")
                );

        assertMatchesContract(objectMapper, event, "learning-path-recommended-event.json");
    }
}
