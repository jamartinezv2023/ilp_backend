package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

final class AssessmentScientificContextReader {

    private final Map<String, ?> context;

    AssessmentScientificContextReader(
            Map<String, ?> context
    ) {
        this.context = context == null
                ? Map.of()
                : Map.copyOf(context);
    }

    String text(
            String key
    ) {
        Object value = context.get(key);

        if (value == null) {
            return null;
        }

        String normalized = value
                .toString()
                .trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    Long longValue(
            String key
    ) {
        Object value = context.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        try {
            return Long.valueOf(
                    value.toString().trim()
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Context value "
                            + key
                            + " must be numeric",
                    exception
            );
        }
    }

    Instant instant(
            String key
    ) {
        Object value = context.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Instant instant) {
            return instant;
        }

        try {
            return Instant.parse(
                    value.toString().trim()
            );
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Context value "
                            + key
                            + " must be an ISO-8601 instant",
                    exception
            );
        }
    }

    Map<String, Object> completeContext() {
        Map<String, Object> copy =
                new LinkedHashMap<>();

        context.forEach(
                copy::put
        );

        return Map.copyOf(copy);
    }
}