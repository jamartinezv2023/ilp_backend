package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "scientific_feature_generation_runs")
public class ScientificFeatureGenerationRunEntity {

    @Id
    @Column(
            name = "id",
            nullable = false,
            length = 100
    )
    private String id;

    @Column(
            name = "participant_id",
            nullable = false,
            length = 100
    )
    private String participantId;

    @Column(
            name = "feature_set_version",
            nullable = false,
            length = 100
    )
    private String featureSetVersion;

    @Column(
            name = "generator_version",
            nullable = false,
            length = 100
    )
    private String generatorVersion;

    @Column(
            name = "feature_cutoff_at",
            nullable = false
    )
    private Instant featureCutoffAt;

    @Column(
            name = "started_at",
            nullable = false
    )
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private ScientificFeatureGenerationStatus status;

    @Column(
            name = "input_observation_count",
            nullable = false
    )
    private Integer inputObservationCount;

    @Column(name = "error_message")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_vector_id")
    private ScientificFeatureVectorEntity featureVector;

    protected ScientificFeatureGenerationRunEntity() {
    }

    public ScientificFeatureGenerationRunEntity(
            String id,
            String participantId,
            String featureSetVersion,
            String generatorVersion,
            Instant featureCutoffAt,
            Instant startedAt,
            Integer inputObservationCount
    ) {
        this.id = requireText(id, "id");
        this.participantId =
                requireText(
                        participantId,
                        "participantId"
                );
        this.featureSetVersion =
                requireText(
                        featureSetVersion,
                        "featureSetVersion"
                );
        this.generatorVersion =
                requireText(
                        generatorVersion,
                        "generatorVersion"
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

        if (
                inputObservationCount == null
                        || inputObservationCount < 0
        ) {
            throw new IllegalArgumentException(
                    "inputObservationCount must be non-negative"
            );
        }

        this.inputObservationCount =
                inputObservationCount;

        this.status =
                ScientificFeatureGenerationStatus.STARTED;
    }

    public void complete(
            ScientificFeatureVectorEntity featureVector,
            Instant completedAt
    ) {
        if (
                status
                        != ScientificFeatureGenerationStatus.STARTED
        ) {
            throw new IllegalStateException(
                    "Only STARTED runs can be completed"
            );
        }

        this.featureVector =
                Objects.requireNonNull(
                        featureVector,
                        "featureVector is required"
                );

        this.completedAt =
                Objects.requireNonNull(
                        completedAt,
                        "completedAt is required"
                );

        if (this.completedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException(
                    "completedAt must not be before startedAt"
            );
        }

        this.errorMessage = null;
        this.status =
                ScientificFeatureGenerationStatus.COMPLETED;
    }

    public void fail(
            String errorMessage,
            Instant completedAt
    ) {
        if (
                status
                        != ScientificFeatureGenerationStatus.STARTED
        ) {
            throw new IllegalStateException(
                    "Only STARTED runs can fail"
            );
        }

        this.errorMessage =
                requireText(
                        errorMessage,
                        "errorMessage"
                );

        this.completedAt =
                Objects.requireNonNull(
                        completedAt,
                        "completedAt is required"
                );

        if (this.completedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException(
                    "completedAt must not be before startedAt"
            );
        }

        this.featureVector = null;
        this.status =
                ScientificFeatureGenerationStatus.FAILED;
    }

    public String getId() {
        return id;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getFeatureSetVersion() {
        return featureSetVersion;
    }

    public String getGeneratorVersion() {
        return generatorVersion;
    }

    public Instant getFeatureCutoffAt() {
        return featureCutoffAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public ScientificFeatureGenerationStatus getStatus() {
        return status;
    }

    public Integer getInputObservationCount() {
        return inputObservationCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ScientificFeatureVectorEntity getFeatureVector() {
        return featureVector;
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