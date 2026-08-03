package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ScientificFeatureVectorResult(
        String vectorId,
        String participantId,
        String featureSetVersion,
        String generatorVersion,
        Instant featureCutoffAt,
        Instant generatedAt,
        int sourceObservationCount,
        String checksum,
        List<ScientificFeatureItemResult> features
) {

    public ScientificFeatureVectorResult {
        vectorId = requireText(
                vectorId,
                "vectorId"
        );

        participantId = requireText(
                participantId,
                "participantId"
        );

        featureSetVersion = requireText(
                featureSetVersion,
                "featureSetVersion"
        );

        generatorVersion = requireText(
                generatorVersion,
                "generatorVersion"
        );

        Objects.requireNonNull(
                featureCutoffAt,
                "featureCutoffAt is required"
        );

        Objects.requireNonNull(
                generatedAt,
                "generatedAt is required"
        );

        if (generatedAt.isBefore(featureCutoffAt)) {
            throw new IllegalArgumentException(
                    "generatedAt must not be before featureCutoffAt"
            );
        }

        if (sourceObservationCount < 0) {
            throw new IllegalArgumentException(
                    "sourceObservationCount must be non-negative"
            );
        }

        checksum = requireText(
                checksum,
                "checksum"
        );

        Objects.requireNonNull(
                features,
                "features are required"
        );

        if (features.isEmpty()) {
            throw new IllegalArgumentException(
                    "features must not be empty"
            );
        }

        if (features.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "features must not contain null elements"
            );
        }

        features = List.copyOf(features);
    }

    public int featureCount() {
        return features.size();
    }

    private static String requireText(
            String value,
            String field
    ) {
        Objects.requireNonNull(
                value,
                field + " is required"
        );

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    field + " must not be blank"
            );
        }

        return normalized;
    }
}
