package com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity;

import java.util.Objects;
import java.util.UUID;

public record ResearchSubjectId(
        UUID value
) {

    public ResearchSubjectId {
        Objects.requireNonNull(
                value,
                "value is required"
        );
    }

    public static ResearchSubjectId generate() {
        return new ResearchSubjectId(
                UUID.randomUUID()
        );
    }
}
