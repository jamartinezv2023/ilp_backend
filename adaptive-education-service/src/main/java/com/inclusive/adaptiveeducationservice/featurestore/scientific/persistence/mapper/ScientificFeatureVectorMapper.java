package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorStatus;

import java.util.List;
import java.util.Objects;

public final class ScientificFeatureVectorMapper {

    private final ScientificFeatureItemMapper itemMapper;

    public ScientificFeatureVectorMapper(
            ScientificFeatureItemMapper itemMapper
    ) {
        this.itemMapper =
                Objects.requireNonNull(
                        itemMapper,
                        "itemMapper is required"
                );
    }

    public ScientificFeatureVectorEntity toEntity(
            ScientificFeatureVector domain
    ) {
        Objects.requireNonNull(
                domain,
                "domain scientific feature vector is required"
        );

        ScientificFeatureVectorEntity entity =
                new ScientificFeatureVectorEntity(
                        domain.id().value(),
                        domain.participantId().value(),
                        domain.featureSetVersion().value(),
                        domain.generatorVersion().value(),
                        domain.featureCutoffAt(),
                        domain.generatedAt(),
                        domain.sourceObservationCount(),
                        ScientificFeatureVectorStatus.COMPLETED,
                        domain.checksum().value()
                );

        domain.items()
                .stream()
                .map(itemMapper::toEntity)
                .forEach(entity::addItem);

        return entity;
    }

    public ScientificFeatureVector toDomain(
            ScientificFeatureVectorEntity entity
    ) {
        Objects.requireNonNull(
                entity,
                "scientific feature vector entity is required"
        );

        requireCompleted(entity);

        List<ScientificFeatureItem> items =
                entity.getItems()
                        .stream()
                        .map(itemMapper::toDomain)
                        .toList();

        return new ScientificFeatureVector(
                new ScientificFeatureVectorId(
                        entity.getId()
                ),
                new ParticipantId(
                        entity.getParticipantId()
                ),
                new FeatureSetVersion(
                        entity.getFeatureSetVersion()
                ),
                new GeneratorVersion(
                        entity.getGeneratorVersion()
                ),
                entity.getFeatureCutoffAt(),
                entity.getGeneratedAt(),
                requireObservationCount(entity),
                new ScientificChecksum(
                        entity.getChecksum()
                ),
                items
        );
    }

    private void requireCompleted(
            ScientificFeatureVectorEntity entity
    ) {
        ScientificFeatureVectorStatus status =
                Objects.requireNonNull(
                        entity.getStatus(),
                        "scientific feature vector status is required"
                );

        if (
                status
                        != ScientificFeatureVectorStatus.COMPLETED
        ) {
            throw new IllegalArgumentException(
                    "Only COMPLETED scientific feature vectors "
                            + "can be mapped to the domain: "
                            + entity.getId()
                            + " has status "
                            + status
            );
        }
    }

    private int requireObservationCount(
            ScientificFeatureVectorEntity entity
    ) {
        Integer count =
                Objects.requireNonNull(
                        entity.getSourceObservationCount(),
                        "sourceObservationCount is required"
                );

        if (count < 0) {
            throw new IllegalArgumentException(
                    "sourceObservationCount must be non-negative"
            );
        }

        return count;
    }
}