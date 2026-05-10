package com.inclusive.common.events.testing.contracts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class EventContractValidator {

    private EventContractValidator() {
    }

    public static void assertMatchesContract(
            ObjectMapper objectMapper,
            Object event,
            String contractFile
    ) throws Exception {

        JsonNode payload =
                objectMapper.valueToTree(event);

        JsonNode contract =
                readContract(objectMapper, contractFile);

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

        JsonNode fieldTypes =
                contract.get("fieldTypes");

        fieldTypes.fields().forEachRemaining(entry -> {

            String fieldName = entry.getKey();

            String expectedType =
                    entry.getValue().asText();

            assertTrue(
                    payload.has(fieldName),
                    "Missing typed field: " + fieldName
            );

            assertJsonType(
                    fieldName,
                    payload.get(fieldName),
                    expectedType
            );
        });
    }

    private static JsonNode readContract(
            ObjectMapper objectMapper,
            String fileName
    ) throws Exception {

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

    private static void assertJsonType(
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
                "Field " + fieldName
                        + " must be "
                        + expectedType
        );
    }
}
