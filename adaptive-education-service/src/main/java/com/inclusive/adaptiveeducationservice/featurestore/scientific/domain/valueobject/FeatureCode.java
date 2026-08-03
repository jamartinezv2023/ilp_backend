package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record FeatureCode(String value) {

    private static final Pattern VALID_PATTERN =
            Pattern.compile("[A-Z][A-Z0-9_]{0,99}");

    public FeatureCode {
        Objects.requireNonNull(
                value,
                "featureCode is required"
        );

        value = value
                .trim()
                .toUpperCase(Locale.ROOT);

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "featureCode must not be blank"
            );
        }

        if (!VALID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "featureCode must start with a letter "
                            + "and contain only uppercase letters, "
                            + "numbers or underscores"
            );
        }
    }
}