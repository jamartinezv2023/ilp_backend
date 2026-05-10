package com.inclusive.common.events.v1.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inclusive.common.events.v1.AssessmentCompletedEvent;
import com.inclusive.common.events.v1.LearningPathInferenceRequestedEvent;
import com.inclusive.common.events.v1.LearningPathRecommendedEvent;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertMatchesContract(event, "assessment-completed-event.json");
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

        assertMatchesContract(event, "learning-path-recommended-event.json");
    }

    private void assertMatchesContract(
            Object event,
            String contractFile
    ) throws Exception {

        JsonNode payload = objectMapper.valueToTree(event);
        JsonNode contract = readContract(contractFile);

        assertEquals(
                contract.get("schemaVersion").asText(),
                payload.get("schemaVersion").asText()
        );

        for (JsonNode field : contract.get("requiredFields")) {
            String fieldName = field.asText();

            assertTrue(
                    payload.hasNonNull(fieldName),
                    "Missing required field: " + fieldName
            );
        }

        JsonNode fieldTypes = contract.get("fieldTypes");

        fieldTypes.fields().forEachRemaining(entry -> {
            String fieldName = entry.getKey();
            String expectedType = entry.getValue().asText();

            assertTrue(
                    payload.has(fieldName),
                    "Missing typed field: " + fieldName
            );

            assertJsonType(fieldName, payload.get(fieldName), expectedType);
        });
    }

    private JsonNode readContract(String fileName) throws Exception {
        Path contractPath = Path.of(
                "contracts",
                "events",
                "v1",
                fileName
        );

        if (!Files.exists(contractPath)) {
            contractPath = Path.of(
                    "..",
                    "contracts",
                    "events",
                    "v1",
                    fileName
            );
        }

        return objectMapper.readTree(contractPath.toFile());
    }

    private void assertJsonType(
            String fieldName,
            JsonNode value,
            String expectedType
    ) {
        boolean valid = switch (expectedType) {
            case "string" -> value.isTextual();
            case "number" -> value.isNumber();
            case "array" -> value.isArray();
            case "object" -> value.isObject();
            case "boolean" -> value.isBoolean();
            default -> false;
        };

        assertTrue(
                valid,
                "Field " + fieldName + " must be " + expectedType
        );
    }
}
