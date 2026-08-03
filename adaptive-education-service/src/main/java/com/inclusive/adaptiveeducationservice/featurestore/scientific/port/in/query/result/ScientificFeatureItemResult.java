package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(
        name = "ScientificFeatureItem",
        description = "Single scientific feature and its typed value."
)
public record ScientificFeatureItemResult(
        @Schema(
                description = "Stable identifier of the feature item.",
                example = "ITEM-REST-001"
        )
        String itemId,

        @Schema(
                description = "Scientific code that identifies the feature.",
                example = "KOLB_CE"
        )
        String featureCode,

        @Schema(
                description = "Data type used by the scientific value.",
                example = "NUMERIC",
                allowableValues = {
                        "NUMERIC",
                        "TEXT",
                        "BOOLEAN"
                }
        )
        DataType dataType,

        @Schema(
                description = "Numeric value when dataType is NUMERIC.",
                example = "25.0"
        )
        Double numericValue,

        @Schema(
                description = "Text value when dataType is TEXT.",
                example = "DIVERGING"
        )
        String textValue,

        @Schema(
                description = "Boolean value when dataType is BOOLEAN.",
                example = "true"
        )
        Boolean booleanValue,

        @Schema(
                description = "Assessment code from which the feature was derived.",
                example = "KOLB"
        )
        String sourceAssessmentCode,

        @Schema(
                description = "Administration identifier supporting scientific traceability.",
                example = "ADMIN-REST-001"
        )
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

        switch (dataType) {
            case NUMERIC -> validateNumeric(
                    numericValue,
                    textValue,
                    booleanValue
            );

            case TEXT -> {
                validateText(
                        numericValue,
                        textValue,
                        booleanValue
                );

                textValue = requireText(
                        textValue,
                        "textValue"
                );
            }

            case BOOLEAN -> validateBoolean(
                    numericValue,
                    textValue,
                    booleanValue
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

    private static void validateNumeric(
            Double numericValue,
            String textValue,
            Boolean booleanValue
    ) {
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

    private static void validateText(
            Double numericValue,
            String textValue,
            Boolean booleanValue
    ) {
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

    private static void validateBoolean(
            Double numericValue,
            String textValue,
            Boolean booleanValue
    ) {
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
