package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import java.util.Objects;

public record FeatureSetVersion(String value) {

    private static final int MAXIMUM_LENGTH = 100;

    public FeatureSetVersion {
        Objects.requireNonNull(
                value,
                "featureSetVersion is required"
        );

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "featureSetVersion must not be blank"
            );
        }

        if (value.length() > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "featureSetVersion must not exceed "
                            + "100 characters"
            );
        }
    }
}