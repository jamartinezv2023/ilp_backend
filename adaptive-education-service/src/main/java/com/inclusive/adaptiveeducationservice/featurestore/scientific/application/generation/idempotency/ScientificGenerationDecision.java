package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.idempotency;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;

import java.util.Objects;
import java.util.Optional;

public record ScientificGenerationDecision(
        Action action,
        ScientificFeatureVector existingVector
) {

    public enum Action {
        GENERATE_NEW,
        REUSE_EXISTING
    }

    public ScientificGenerationDecision {
        Objects.requireNonNull(
                action,
                "action is required"
        );

        if (
                action == Action.REUSE_EXISTING
                        && existingVector == null
        ) {
            throw new IllegalArgumentException(
                    "existingVector is required when reusing"
            );
        }

        if (
                action == Action.GENERATE_NEW
                        && existingVector != null
        ) {
            throw new IllegalArgumentException(
                    "existingVector must be null when generating"
            );
        }
    }

    public static ScientificGenerationDecision generateNew() {
        return new ScientificGenerationDecision(
                Action.GENERATE_NEW,
                null
        );
    }

    public static ScientificGenerationDecision reuse(
            ScientificFeatureVector vector
    ) {
        return new ScientificGenerationDecision(
                Action.REUSE_EXISTING,
                Objects.requireNonNull(
                        vector,
                        "vector is required"
                )
        );
    }

    public boolean shouldGenerate() {
        return action == Action.GENERATE_NEW;
    }

    public boolean shouldReuse() {
        return action == Action.REUSE_EXISTING;
    }

    public Optional<ScientificFeatureVector> vector() {
        return Optional.ofNullable(
                existingVector
        );
    }
}