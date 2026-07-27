package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import java.util.Objects;

public record ScientificFeatureVectorId(String value) {

    private static final int MAXIMUM_LENGTH = 100;

    public ScientificFeatureVectorId {
        Objects.requireNonNull(
                value,
                "scientificFeatureVectorId is required"
        );

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "scientificFeatureVectorId must not be blank"
            );
        }

        if (value.length() > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "scientificFeatureVectorId must not exceed "
                            + "100 characters"
            );
        }
    }
}