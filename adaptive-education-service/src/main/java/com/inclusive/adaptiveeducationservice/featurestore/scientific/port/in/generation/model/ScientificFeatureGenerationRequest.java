package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ScientificFeatureGenerationRequest(
        ScientificFeatureVectorId vectorId,
        ParticipantId participantId,
        FeatureSetVersion featureSetVersion,
        GeneratorVersion generatorVersion,
        Instant featureCutoffAt,
        Instant generatedAt,
        int inputObservationCount,
        ScientificChecksum checksum,
        List<ScientificFeatureItem> extractedFeatures
) {

    public ScientificFeatureGenerationRequest {
        Objects.requireNonNull(
                vectorId,
                "vectorId is required"
        );

        Objects.requireNonNull(
                participantId,
                "participantId is required"
        );

        Objects.requireNonNull(
                featureSetVersion,
                "featureSetVersion is required"
        );

        Objects.requireNonNull(
                generatorVersion,
                "generatorVersion is required"
        );

        Objects.requireNonNull(
                featureCutoffAt,
                "featureCutoffAt is required"
        );

        Objects.requireNonNull(
                generatedAt,
                "generatedAt is required"
        );

        Objects.requireNonNull(
                checksum,
                "checksum is required"
        );

        if (generatedAt.isBefore(featureCutoffAt)) {
            throw new IllegalArgumentException(
                    "generatedAt must not be before featureCutoffAt"
            );
        }

        if (inputObservationCount < 0) {
            throw new IllegalArgumentException(
                    "inputObservationCount must be non-negative"
            );
        }

        if (extractedFeatures == null
                || extractedFeatures.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one extracted feature is required"
            );
        }

        if (extractedFeatures.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "extractedFeatures must not contain null elements"
            );
        }

        extractedFeatures =
                List.copyOf(extractedFeatures);
    }

    public int featureCount() {
        return extractedFeatures.size();
    }
}