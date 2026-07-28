package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class ScientificFeatureGenerationRun {

    private static final int MAXIMUM_ERROR_LENGTH = 2000;

    private final ScientificFeatureGenerationRunId id;
    private final ParticipantId participantId;
    private final FeatureSetVersion featureSetVersion;
    private final GeneratorVersion generatorVersion;
    private final Instant featureCutoffAt;
    private final Instant startedAt;
    private final Instant completedAt;
    private final ScientificFeatureGenerationStatus status;
    private final int inputObservationCount;
    private final String errorMessage;
    private final ScientificFeatureVectorId featureVectorId;

    private ScientificFeatureGenerationRun(
            ScientificFeatureGenerationRunId id,
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion,
            GeneratorVersion generatorVersion,
            Instant featureCutoffAt,
            Instant startedAt,
            Instant completedAt,
            ScientificFeatureGenerationStatus status,
            int inputObservationCount,
            String errorMessage,
            ScientificFeatureVectorId featureVectorId
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

        this.startedAt =
                Objects.requireNonNull(
                        startedAt,
                        "startedAt is required"
                );

        this.status =
                Objects.requireNonNull(
                        status,
                        "status is required"
                );

        if (inputObservationCount < 0) {
            throw new IllegalArgumentException(
                    "inputObservationCount must be non-negative"
            );
        }

        this.inputObservationCount =
                inputObservationCount;

        this.completedAt = completedAt;
        this.errorMessage =
                normalizeErrorMessage(errorMessage);
        this.featureVectorId = featureVectorId;

        validateState();
    }

    public static ScientificFeatureGenerationRun start(
            ScientificFeatureGenerationRunId id,
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion,
            GeneratorVersion generatorVersion,
            Instant featureCutoffAt,
            Instant startedAt,
            int inputObservationCount
    ) {
        return new ScientificFeatureGenerationRun(
                id,
                participantId,
                featureSetVersion,
                generatorVersion,
                featureCutoffAt,
                startedAt,
                null,
                ScientificFeatureGenerationStatus.STARTED,
                inputObservationCount,
                null,
                null
        );
    }

    public ScientificFeatureGenerationRun complete(
            ScientificFeatureVectorId featureVectorId,
            Instant completedAt
    ) {
        requireStarted();

        return new ScientificFeatureGenerationRun(
                id,
                participantId,
                featureSetVersion,
                generatorVersion,
                featureCutoffAt,
                startedAt,
                Objects.requireNonNull(
                        completedAt,
                        "completedAt is required"
                ),
                ScientificFeatureGenerationStatus.COMPLETED,
                inputObservationCount,
                null,
                Objects.requireNonNull(
                        featureVectorId,
                        "featureVectorId is required"
                )
        );
    }

    public ScientificFeatureGenerationRun fail(
            String errorMessage,
            Instant completedAt
    ) {
        requireStarted();

        return new ScientificFeatureGenerationRun(
                id,
                participantId,
                featureSetVersion,
                generatorVersion,
                featureCutoffAt,
                startedAt,
                Objects.requireNonNull(
                        completedAt,
                        "completedAt is required"
                ),
                ScientificFeatureGenerationStatus.FAILED,
                inputObservationCount,
                requireErrorMessage(errorMessage),
                null
        );
    }

    public boolean isStarted() {
        return status
                == ScientificFeatureGenerationStatus.STARTED;
    }

    public boolean isCompleted() {
        return status
                == ScientificFeatureGenerationStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status
                == ScientificFeatureGenerationStatus.FAILED;
    }

    public Optional<Duration> duration() {
        if (completedAt == null) {
            return Optional.empty();
        }

        return Optional.of(
                Duration.between(
                        startedAt,
                        completedAt
                )
        );
    }

    public ScientificFeatureGenerationRunId id() {
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

    public Instant startedAt() {
        return startedAt;
    }

    public Instant completedAt() {
        return completedAt;
    }

    public ScientificFeatureGenerationStatus status() {
        return status;
    }

    public int inputObservationCount() {
        return inputObservationCount;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public ScientificFeatureVectorId featureVectorId() {
        return featureVectorId;
    }

    private void requireStarted() {
        if (!isStarted()) {
            throw new IllegalStateException(
                    "Only STARTED runs can change state"
            );
        }
    }

    private void validateState() {
        if (
                completedAt != null
                        && completedAt.isBefore(startedAt)
        ) {
            throw new IllegalArgumentException(
                    "completedAt must not be before startedAt"
            );
        }

        switch (status) {
            case STARTED -> validateStartedState();
            case COMPLETED -> validateCompletedState();
            case FAILED -> validateFailedState();
        }
    }

    private void validateStartedState() {
        if (
                completedAt != null
                        || errorMessage != null
                        || featureVectorId != null
        ) {
            throw new IllegalArgumentException(
                    "STARTED run cannot contain completion data"
            );
        }
    }

    private void validateCompletedState() {
        if (
                completedAt == null
                        || featureVectorId == null
                        || errorMessage != null
        ) {
            throw new IllegalArgumentException(
                    "COMPLETED run requires completedAt "
                            + "and featureVectorId"
            );
        }
    }

    private void validateFailedState() {
        if (
                completedAt == null
                        || errorMessage == null
                        || featureVectorId != null
        ) {
            throw new IllegalArgumentException(
                    "FAILED run requires completedAt "
                            + "and errorMessage"
            );
        }
    }

    private static String requireErrorMessage(
            String value
    ) {
        String normalized =
                normalizeErrorMessage(value);

        if (normalized == null) {
            throw new IllegalArgumentException(
                    "errorMessage must not be blank"
            );
        }

        return normalized;
    }

    private static String normalizeErrorMessage(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > MAXIMUM_ERROR_LENGTH) {
            throw new IllegalArgumentException(
                    "errorMessage must not exceed "
                            + MAXIMUM_ERROR_LENGTH
                            + " characters"
            );
        }

        return normalized;
    }
}