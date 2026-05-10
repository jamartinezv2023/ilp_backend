package com.inclusive.adaptive.contracts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdaptiveEducationConsumerContractTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Test
    void shouldRemainCompatibleWithLearningPathInferenceRequestedEvent()
            throws Exception {

        assertConsumerCompatibility(
                "learning-path-inference-requested-event.json",
                Set.of(
                        "eventId",
                        "correlationId",
                        "schemaVersion",
                        "studentId",
                        "tenantId",
                        "assessmentId",
                        "assessmentType",
                        "score",
                        "result",
                        "policyName",
                        "policyVersion",
                        "candidateItemsCsv",
                        "rationale",
                        "metadata",
                        "occurredAt"
                )
        );
    }

    @Test
    void shouldRemainCompatibleWithLearningPathRecommendedEvent()
            throws Exception {

        assertConsumerCompatibility(
                "learning-path-recommended-event.json",
                Set.of(
                        "eventId",
                        "schemaVersion",
                        "studentId",
                        "tenantId",
                        "recommendationId",
                        "reason",
                        "recommendedItems",
                        "confidence",
                        "policyName",
                        "modelName",
                        "modelVersion",
                        "metadata",
                        "occurredAt"
                )
        );
    }

    private void assertConsumerCompatibility(
            String contractFile,
            Set<String> expectedFields
    ) throws Exception {

        JsonNode contract =
                readContract(contractFile);

        assertEquals(
                "v1",
                contract.get("schemaVersion").asText()
        );

        JsonNode requiredFields =
                contract.get("requiredFields");

        for (String expectedField : expectedFields) {
            assertTrue(
                    contains(requiredFields, expectedField),
                    "Contract missing expected field: "
                            + expectedField
            );
        }
    }

    private JsonNode readContract(String fileName)
            throws Exception {

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

    private boolean contains(
            JsonNode array,
            String expectedValue
    ) {

        for (JsonNode node : array) {
            if (expectedValue.equals(node.asText())) {
                return true;
            }
        }

        return false;
    }
}
