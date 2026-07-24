package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model;

import java.util.Objects;

public record ScientificInterpretation(
        String interpretationCode,
        String interpretationText,
        String interpretationVersion
) {

    public ScientificInterpretation {
        Objects.requireNonNull(
                interpretationCode,
                "interpretationCode is required"
        );

        Objects.requireNonNull(
                interpretationText,
                "interpretationText is required"
        );

        Objects.requireNonNull(
                interpretationVersion,
                "interpretationVersion is required"
        );
    }
}