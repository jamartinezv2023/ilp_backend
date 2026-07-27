package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import java.util.Objects;

public record ParticipantId(String value) {

    private static final int MAXIMUM_LENGTH = 100;

    public ParticipantId {
        Objects.requireNonNull(
                value,
                "participantId is required"
        );

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "participantId must not be blank"
            );
        }

        if (value.length() > MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "participantId must not exceed 100 characters"
            );
        }
    }
}