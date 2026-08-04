package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationRun;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationRunEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;

import java.time.Instant;
import java.util.Objects;

public final class ScientificFeatureGenerationRunMapper {

    public ScientificFeatureGenerationRunEntity toEntity(
            ScientificFeatureGenerationRun domain,
            ScientificFeatureVectorEntity vectorEntity
    ) {
        Objects.requireNonNull(
                domain,
                "domain scientific feature generation run is required"
        );

        ScientificFeatureGenerationRunEntity entity =
                new ScientificFeatureGenerationRunEntity(
                        domain.id().value(),
                        domain.participantId().value(),
                        domain.featureSetVersion().value(),
                        domain.generatorVersion().value(),
                        domain.featureCutoffAt(),
                        domain.startedAt(),
                        domain.inputObservationCount()
                );

        switch (domain.status()) {
            case STARTED ->
                    validateStartedMapping(
                            domain,
                            vectorEntity
                    );

            case COMPLETED ->
                    completeEntity(
                            entity,
                            domain,
                            vectorEntity
                    );

            case FAILED ->
                    failEntity(
                            entity,
                            domain,
                            vectorEntity
                    );
        }

        return entity;
    }

    public ScientificFeatureGenerationRun toDomain(
            ScientificFeatureGenerationRunEntity entity
    ) {
        Objects.requireNonNull(
                entity,
                "scientific feature generation run entity is required"
        );

        ScientificFeatureGenerationRun started =
                ScientificFeatureGenerationRun.start(
                        new ScientificFeatureGenerationRunId(
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
                        entity.getStartedAt(),
                        requireObservationCount(entity)
                );

        com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationStatus
                status =
                Objects.requireNonNull(
                        entity.getStatus(),
                        "scientific feature generation status is required"
                );

        return switch (status) {
            case STARTED ->
                    mapStartedEntity(
                            started,
                            entity
                    );

            case COMPLETED ->
                    mapCompletedEntity(
                            started,
                            entity
                    );

            case FAILED ->
                    mapFailedEntity(
                            started,
                            entity
                    );
        };
    }

    private void validateStartedMapping(
            ScientificFeatureGenerationRun domain,
            ScientificFeatureVectorEntity vectorEntity
    ) {
        if (vectorEntity != null) {
            throw new IllegalArgumentException(
                    "STARTED run must not reference a feature vector"
            );
        }

        if (
                domain.completedAt() != null
                        || domain.featureVectorId() != null
                        || domain.errorMessage() != null
        ) {
            throw new IllegalArgumentException(
                    "STARTED run contains completion data"
            );
        }
    }

    private void completeEntity(
            ScientificFeatureGenerationRunEntity entity,
            ScientificFeatureGenerationRun domain,
            ScientificFeatureVectorEntity vectorEntity
    ) {
        Objects.requireNonNull(
                vectorEntity,
                "vectorEntity is required for COMPLETED run"
        );

        ScientificFeatureVectorId vectorId =
                Objects.requireNonNull(
                        domain.featureVectorId(),
                        "featureVectorId is required for COMPLETED run"
                );

        if (
                !vectorId.value()
                        .equals(vectorEntity.getId())
        ) {
            throw new IllegalArgumentException(
                    "Domain featureVectorId does not match "
                            + "the supplied vector entity"
            );
        }

        entity.complete(
                vectorEntity,
                requireCompletedAt(domain)
        );
    }

    private void failEntity(
            ScientificFeatureGenerationRunEntity entity,
            ScientificFeatureGenerationRun domain,
            ScientificFeatureVectorEntity vectorEntity
    ) {
        if (vectorEntity != null) {
            throw new IllegalArgumentException(
                    "FAILED run must not reference a feature vector"
            );
        }

        String errorMessage =
                Objects.requireNonNull(
                        domain.errorMessage(),
                        "errorMessage is required for FAILED run"
                );

        entity.fail(
                errorMessage,
                requireCompletedAt(domain)
        );
    }

    private ScientificFeatureGenerationRun mapStartedEntity(
            ScientificFeatureGenerationRun started,
            ScientificFeatureGenerationRunEntity entity
    ) {
        if (
                entity.getCompletedAt() != null
                        || entity.getErrorMessage() != null
                        || entity.getFeatureVector() != null
        ) {
            throw inconsistentEntity(
                    entity,
                    "STARTED"
            );
        }

        return started;
    }

    private ScientificFeatureGenerationRun mapCompletedEntity(
            ScientificFeatureGenerationRun started,
            ScientificFeatureGenerationRunEntity entity
    ) {
        ScientificFeatureVectorEntity vector =
                entity.getFeatureVector();

        if (
                vector == null
                        || entity.getCompletedAt() == null
                        || entity.getErrorMessage() != null
        ) {
            throw inconsistentEntity(
                    entity,
                    "COMPLETED"
            );
        }

        return started.complete(
                new ScientificFeatureVectorId(
                        vector.getId()
                ),
                entity.getCompletedAt()
        );
    }

    private ScientificFeatureGenerationRun mapFailedEntity(
            ScientificFeatureGenerationRun started,
            ScientificFeatureGenerationRunEntity entity
    ) {
        if (
                entity.getCompletedAt() == null
                        || entity.getErrorMessage() == null
                        || entity.getFeatureVector() != null
        ) {
            throw inconsistentEntity(
                    entity,
                    "FAILED"
            );
        }

        return started.fail(
                entity.getErrorMessage(),
                entity.getCompletedAt()
        );
    }

    private Instant requireCompletedAt(
            ScientificFeatureGenerationRun domain
    ) {
        return Objects.requireNonNull(
                domain.completedAt(),
                "completedAt is required for finalized run"
        );
    }

    private int requireObservationCount(
            ScientificFeatureGenerationRunEntity entity
    ) {
        Integer count =
                Objects.requireNonNull(
                        entity.getInputObservationCount(),
                        "inputObservationCount is required"
                );

        if (count < 0) {
            throw new IllegalArgumentException(
                    "inputObservationCount must be non-negative"
            );
        }

        return count;
    }

    private IllegalArgumentException inconsistentEntity(
            ScientificFeatureGenerationRunEntity entity,
            String expectedStatus
    ) {
        return new IllegalArgumentException(
                "Inconsistent scientific feature generation run entity "
                        + entity.getId()
                        + " for status "
                        + expectedStatus
        );
    }
}