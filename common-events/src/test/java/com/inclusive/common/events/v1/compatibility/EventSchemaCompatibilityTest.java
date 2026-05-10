package com.inclusive.common.events.v1.compatibility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventSchemaCompatibilityTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void v1ContractsShouldRemainBackwardCompatibleWithBaseline()
            throws Exception {

        Path baselineDirectory =
                Path.of("..", "contracts", "events", "baseline", "v1");

        Path currentDirectory =
                Path.of("..", "contracts", "events", "v1");

        try (var baselineFiles = Files.list(baselineDirectory)) {
            baselineFiles
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> assertBackwardCompatible(
                            path,
                            currentDirectory.resolve(path.getFileName())
                    ));
        }
    }

    private void assertBackwardCompatible(
            Path baselinePath,
            Path currentPath
    ) {

        try {
            assertTrue(
                    Files.exists(currentPath),
                    "Current contract missing: "
                            + currentPath.getFileName()
            );

            JsonNode baseline =
                    objectMapper.readTree(baselinePath.toFile());

            JsonNode current =
                    objectMapper.readTree(currentPath.toFile());

            assertEquals(
                    baseline.get("schemaVersion").asText(),
                    current.get("schemaVersion").asText(),
                    "schemaVersion cannot change inside the same version folder"
            );

            assertEquals(
                    baseline.get("eventType").asText(),
                    current.get("eventType").asText(),
                    "eventType cannot change for "
                            + baselinePath.getFileName()
            );

            assertRequiredFieldsPreserved(
                    baseline.get("requiredFields"),
                    current.get("requiredFields"),
                    baselinePath.getFileName().toString()
            );

            assertFieldTypesPreserved(
                    baseline.get("fieldTypes"),
                    current.get("fieldTypes"),
                    baselinePath.getFileName().toString()
            );

        } catch (Exception exception) {
            throw new AssertionError(
                    "Compatibility check failed for "
                            + baselinePath.getFileName()
                            + ": "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private void assertRequiredFieldsPreserved(
            JsonNode baselineRequiredFields,
            JsonNode currentRequiredFields,
            String contractName
    ) {

        for (JsonNode baselineField : baselineRequiredFields) {
            String fieldName = baselineField.asText();

            assertTrue(
                    contains(currentRequiredFields, fieldName),
                    "Breaking change in "
                            + contractName
                            + ": required field removed: "
                            + fieldName
            );
        }
    }

    private void assertFieldTypesPreserved(
            JsonNode baselineFieldTypes,
            JsonNode currentFieldTypes,
            String contractName
    ) {

        Iterator<Map.Entry<String, JsonNode>> fields =
                baselineFieldTypes.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field =
                    fields.next();

            String fieldName =
                    field.getKey();

            String expectedType =
                    field.getValue().asText();

            assertTrue(
                    currentFieldTypes.has(fieldName),
                    "Breaking change in "
                            + contractName
                            + ": typed field removed: "
                            + fieldName
            );

            assertEquals(
                    expectedType,
                    currentFieldTypes.get(fieldName).asText(),
                    "Breaking change in "
                            + contractName
                            + ": type changed for field "
                            + fieldName
            );
        }
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
