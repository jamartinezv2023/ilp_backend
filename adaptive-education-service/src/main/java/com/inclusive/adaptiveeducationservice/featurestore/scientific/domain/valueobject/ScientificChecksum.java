package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import java.util.Objects;

public record ScientificChecksum(String value) {

    private static final int MAXIMUM_LENGTH = 128;

    public ScientificChecksum {
        Objects.requireNonNull(
                value,
                "scientificChecksum is required"
        );

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "scientificChecksum must not be blank"
            );
        }

        if (value.length() > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "scientificChecksum must not exceed "
                            + "128 characters"
            );
        }
    }
}