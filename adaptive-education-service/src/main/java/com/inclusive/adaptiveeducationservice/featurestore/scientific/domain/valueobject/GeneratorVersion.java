package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import java.util.Objects;

public record GeneratorVersion(String value) {

    private static final int MAXIMUM_LENGTH = 100;

    public GeneratorVersion {
        Objects.requireNonNull(
                value,
                "generatorVersion is required"
        );

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "generatorVersion must not be blank"
            );
        }

        if (value.length() > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "generatorVersion must not exceed "
                            + "100 characters"
            );
        }
    }
}