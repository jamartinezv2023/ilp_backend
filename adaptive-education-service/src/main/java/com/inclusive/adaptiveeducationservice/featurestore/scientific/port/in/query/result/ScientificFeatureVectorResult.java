package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Schema(
        name = "ScientificFeatureVector",
        description = "Immutable scientific feature vector exposed by query operations."
)
public record ScientificFeatureVectorResult(
        @Schema(
                description = "Stable identifier of the scientific vector.",
                example = "VECTOR-EXACT-REST-001"
        )
        String vectorId,

        @Schema(
                description = "Participant associated with the vector.",
                example = "PARTICIPANT-REST-001"
        )
        String participantId,

        @Schema(
                description = "Version of the scientific feature set.",
                example = "SCIENTIFIC-FEATURES-V1"
        )
        String featureSetVersion,

        @Schema(
                description = "Version of the generator that produced the vector.",
                example = "GENERATOR-V1"
        )
        String generatorVersion,

        @Schema(
                description = "Scientific cutoff instant used to select source observations.",
                type = "string",
                format = "date-time",
                example = "2026-08-02T12:00:00Z"
        )
        Instant featureCutoffAt,

        @Schema(
                description = "Instant at which the vector was generated.",
                type = "string",
                format = "date-time",
                example = "2026-08-02T12:00:05Z"
        )
        Instant generatedAt,

        @Schema(
                description = "Number of source observations used during generation.",
                example = "1",
                minimum = "0"
        )
        int sourceObservationCount,

        @Schema(
                description = "Scientific checksum protecting vector integrity.",
                example = "CHECKSUM-REST-001"
        )
        String checksum,

        @ArraySchema(
                arraySchema = @Schema(
                        description = "Ordered scientific features contained in the vector."
                ),
                schema = @Schema(
                        implementation = ScientificFeatureItemResult.class
                ),
                minItems = 1
        )
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

    @Schema(
            description = "Number of features contained in the vector.",
            example = "1",
            minimum = "1"
    )
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
