package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;

import java.util.Objects;

public record ScientificFeatureItem(
        String id,
        FeatureCode featureCode,
        FeatureValue value,
        String sourceAssessmentCode,
        String sourceAdministrationId
) {

    private static final int MAXIMUM_ID_LENGTH = 120;
    private static final int MAXIMUM_SOURCE_LENGTH = 100;

    public ScientificFeatureItem {
        id = requireText(
                id,
                "id",
                MAXIMUM_ID_LENGTH
        );

        Objects.requireNonNull(
                featureCode,
                "featureCode is required"
        );

        Objects.requireNonNull(
                value,
                "featureValue is required"
        );

        sourceAssessmentCode =
                normalizeOptional(
                        sourceAssessmentCode,
                        "sourceAssessmentCode",
                        MAXIMUM_SOURCE_LENGTH
                );

        sourceAdministrationId =
                normalizeOptional(
                        sourceAdministrationId,
                        "sourceAdministrationId",
                        MAXIMUM_SOURCE_LENGTH
                );
    }

    private static String requireText(
            String value,
            String field,
            int maximumLength
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

        if (normalized.length() > maximumLength) {
            throw new IllegalArgumentException(
                    field
                            + " must not exceed "
                            + maximumLength
                            + " characters"
            );
        }

        return normalized;
    }

    private static String normalizeOptional(
            String value,
            String field,
            int maximumLength
    ) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > maximumLength) {
            throw new IllegalArgumentException(
                    field
                            + " must not exceed "
                            + maximumLength
                            + " characters"
            );
        }

        return normalized;
    }
}