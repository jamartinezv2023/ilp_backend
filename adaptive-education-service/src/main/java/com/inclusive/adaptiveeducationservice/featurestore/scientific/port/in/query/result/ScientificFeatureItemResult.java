package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result;

import java.util.Objects;

public record ScientificFeatureItemResult(
        String itemId,
        String featureCode,
        DataType dataType,
        Double numericValue,
        String textValue,
        Boolean booleanValue,
        String sourceAssessmentCode,
        String sourceAdministrationId
) {

    public enum DataType {
        NUMERIC,
        TEXT,
        BOOLEAN
    }

    public ScientificFeatureItemResult {
        itemId = requireText(
                itemId,
                "itemId"
        );

        featureCode = requireText(
                featureCode,
                "featureCode"
        );

        Objects.requireNonNull(
                dataType,
                "dataType is required"
        );

        sourceAssessmentCode =
                normalizeOptional(
                        sourceAssessmentCode
                );

        sourceAdministrationId =
                normalizeOptional(
                        sourceAdministrationId
                );

        validateValue(
                dataType,
                numericValue,
                textValue,
                booleanValue
        );

        if (textValue != null) {
            textValue = requireText(
                    textValue,
                    "textValue"
            );
        }
    }

    public static ScientificFeatureItemResult numeric(
            String itemId,
            String featureCode,
            double value,
            String sourceAssessmentCode,
            String sourceAdministrationId
    ) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(
                    "numericValue must be finite"
            );
        }

        return new ScientificFeatureItemResult(
                itemId,
                featureCode,
                DataType.NUMERIC,
                value,
                null,
                null,
                sourceAssessmentCode,
                sourceAdministrationId
        );
    }

    public static ScientificFeatureItemResult text(
            String itemId,
            String featureCode,
            String value,
            String sourceAssessmentCode,
            String sourceAdministrationId
    ) {
        return new ScientificFeatureItemResult(
                itemId,
                featureCode,
                DataType.TEXT,
                null,
                value,
                null,
                sourceAssessmentCode,
                sourceAdministrationId
        );
    }

    public static ScientificFeatureItemResult bool(
            String itemId,
            String featureCode,
            boolean value,
            String sourceAssessmentCode,
            String sourceAdministrationId
    ) {
        return new ScientificFeatureItemResult(
                itemId,
                featureCode,
                DataType.BOOLEAN,
                null,
                null,
                value,
                sourceAssessmentCode,
                sourceAdministrationId
        );
    }

    private static void validateValue(
            DataType dataType,
            Double numericValue,
            String textValue,
            Boolean booleanValue
    ) {
        switch (dataType) {
            case NUMERIC -> {
                if (
                        numericValue == null
                                || textValue != null
                                || booleanValue != null
                ) {
                    throw new IllegalArgumentException(
                            "NUMERIC requires only numericValue"
                    );
                }

                if (!Double.isFinite(numericValue)) {
                    throw new IllegalArgumentException(
                            "numericValue must be finite"
                    );
                }
            }

            case TEXT -> {
                if (
                        textValue == null
                                || numericValue != null
                                || booleanValue != null
                ) {
                    throw new IllegalArgumentException(
                            "TEXT requires only textValue"
                    );
                }
            }

            case BOOLEAN -> {
                if (
                        booleanValue == null
                                || numericValue != null
                                || textValue != null
                ) {
                    throw new IllegalArgumentException(
                            "BOOLEAN requires only booleanValue"
                    );
                }
            }
        }
    }

    private static String requireText(
            String value,
            String field
    ) {
        Objects.requireNonNull(
                value,
                field + " is required"
        );

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    field + " must not be blank"
            );
        }

        return normalized;
    }

    private static String normalizeOptional(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}
