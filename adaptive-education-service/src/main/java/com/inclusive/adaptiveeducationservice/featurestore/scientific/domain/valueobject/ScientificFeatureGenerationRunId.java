package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import java.util.Objects;

public record ScientificFeatureGenerationRunId(
        String value
) {

    private static final int MAXIMUM_LENGTH = 100;

    public ScientificFeatureGenerationRunId {
        Objects.requireNonNull(
                value,
                "scientificFeatureGenerationRunId is required"
        );

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "scientificFeatureGenerationRunId "
                            + "must not be blank"
            );
        }

        if (value.length() > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "scientificFeatureGenerationRunId "
                            + "must not exceed 100 characters"
            );
        }
    }
}