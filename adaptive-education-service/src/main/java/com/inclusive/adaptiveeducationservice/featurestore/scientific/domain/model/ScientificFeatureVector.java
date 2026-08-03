package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ScientificFeatureVector {

    private final ScientificFeatureVectorId id;
    private final ParticipantId participantId;
    private final FeatureSetVersion featureSetVersion;
    private final GeneratorVersion generatorVersion;
    private final Instant featureCutoffAt;
    private final Instant generatedAt;
    private final int sourceObservationCount;
    private final ScientificChecksum checksum;
    private final List<ScientificFeatureItem> items;
    private final Map<FeatureCode, ScientificFeatureItem> itemsByCode;

    public ScientificFeatureVector(
            ScientificFeatureVectorId id,
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion,
            GeneratorVersion generatorVersion,
            Instant featureCutoffAt,
            Instant generatedAt,
            int sourceObservationCount,
            ScientificChecksum checksum,
            List<ScientificFeatureItem> items
    ) {
        this.id =
                Objects.requireNonNull(
                        id,
                        "id is required"
                );

        this.participantId =
                Objects.requireNonNull(
                        participantId,
                        "participantId is required"
                );

        this.featureSetVersion =
                Objects.requireNonNull(
                        featureSetVersion,
                        "featureSetVersion is required"
                );

        this.generatorVersion =
                Objects.requireNonNull(
                        generatorVersion,
                        "generatorVersion is required"
                );

        this.featureCutoffAt =
                Objects.requireNonNull(
                        featureCutoffAt,
                        "featureCutoffAt is required"
                );

        this.generatedAt =
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

        this.sourceObservationCount =
                sourceObservationCount;

        this.checksum =
                Objects.requireNonNull(
                        checksum,
                        "checksum is required"
                );

        this.itemsByCode =
                validateAndIndexItems(items);

        this.items =
                List.copyOf(
                        itemsByCode.values()
                );
    }

    public ScientificFeatureVectorId id() {
        return id;
    }

    public ParticipantId participantId() {
        return participantId;
    }

    public FeatureSetVersion featureSetVersion() {
        return featureSetVersion;
    }

    public GeneratorVersion generatorVersion() {
        return generatorVersion;
    }

    public Instant featureCutoffAt() {
        return featureCutoffAt;
    }

    public Instant generatedAt() {
        return generatedAt;
    }

    public int sourceObservationCount() {
        return sourceObservationCount;
    }

    public ScientificChecksum checksum() {
        return checksum;
    }

    public List<ScientificFeatureItem> items() {
        return items;
    }

    public int featureCount() {
        return items.size();
    }

    public boolean containsFeature(
            FeatureCode featureCode
    ) {
        Objects.requireNonNull(
                featureCode,
                "featureCode is required"
        );

        return itemsByCode.containsKey(
                featureCode
        );
    }

    public Optional<ScientificFeatureItem> findFeature(
            FeatureCode featureCode
    ) {
        Objects.requireNonNull(
                featureCode,
                "featureCode is required"
        );

        return Optional.ofNullable(
                itemsByCode.get(featureCode)
        );
    }

    public ScientificFeatureItem requireFeature(
            FeatureCode featureCode
    ) {
        return findFeature(featureCode)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Feature not found: "
                                        + featureCode.value()
                        )
                );
    }

    private static Map<
            FeatureCode,
            ScientificFeatureItem
            > validateAndIndexItems(
            List<ScientificFeatureItem> items
    ) {
        Objects.requireNonNull(
                items,
                "items are required"
        );

        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                    "items must not be empty"
            );
        }

        Map<
                FeatureCode,
                ScientificFeatureItem
                > indexed =
                new LinkedHashMap<>();

        for (ScientificFeatureItem item : items) {
            Objects.requireNonNull(
                    item,
                    "feature item is required"
            );

            ScientificFeatureItem previous =
                    indexed.putIfAbsent(
                            item.featureCode(),
                            item
                    );

            if (previous != null) {
                throw new IllegalArgumentException(
                        "Duplicate feature code: "
                                + item.featureCode().value()
                );
            }
        }

        return Map.copyOf(indexed);
    }
}