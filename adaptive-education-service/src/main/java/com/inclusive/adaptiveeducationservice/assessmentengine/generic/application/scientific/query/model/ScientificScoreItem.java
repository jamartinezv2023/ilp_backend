package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model;

import java.util.Objects;

public record ScientificScoreItem(
        String dimensionCode,
        Double numericValue
) {

    public ScientificScoreItem {
        Objects.requireNonNull(
                dimensionCode,
                "dimensionCode is required"
        );

        Objects.requireNonNull(
                numericValue,
                "numericValue is required"
        );
    }
}