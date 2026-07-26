package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "scientific_feature_vectors",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_scientific_feature_vector_identity",
                        columnNames = {
                                "participant_id",
                                "feature_set_version",
                                "feature_cutoff_at"
                        }
                )
        }
)
public class ScientificFeatureVectorEntity {

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
            name = "generated_at",
            nullable = false
    )
    private Instant generatedAt;

    @Column(
            name = "source_observation_count",
            nullable = false
    )
    private Integer sourceObservationCount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private ScientificFeatureVectorStatus status;

    @Column(
            name = "checksum",
            nullable = false,
            length = 128
    )
    private String checksum;

    @OneToMany(
            mappedBy = "featureVector",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ScientificFeatureItemEntity> items =
            new ArrayList<>();

    protected ScientificFeatureVectorEntity() {
    }

    public ScientificFeatureVectorEntity(
            String id,
            String participantId,
            String featureSetVersion,
            String generatorVersion,
            Instant featureCutoffAt,
            Instant generatedAt,
            Integer sourceObservationCount,
            ScientificFeatureVectorStatus status,
            String checksum
    ) {
        this.id = requireText(id, "id");
        this.participantId =
                requireText(participantId, "participantId");
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
        this.generatedAt =
                Objects.requireNonNull(
                        generatedAt,
                        "generatedAt is required"
                );

        if (
                sourceObservationCount == null
                        || sourceObservationCount < 0
        ) {
            throw new IllegalArgumentException(
                    "sourceObservationCount must be non-negative"
            );
        }

        this.sourceObservationCount =
                sourceObservationCount;

        this.status =
                Objects.requireNonNull(
                        status,
                        "status is required"
                );

        this.checksum =
                requireText(checksum, "checksum");
    }

    public void addItem(
            ScientificFeatureItemEntity item
    ) {
        Objects.requireNonNull(
                item,
                "item is required"
        );

        boolean duplicatedCode =
                items.stream()
                        .anyMatch(existing ->
                                existing.getFeatureCode()
                                        .equals(
                                                item.getFeatureCode()
                                        )
                        );

        if (duplicatedCode) {
            throw new IllegalArgumentException(
                    "Duplicate feature code: "
                            + item.getFeatureCode()
            );
        }

        item.assignFeatureVector(this);
        items.add(item);
    }

    public void removeItem(
            ScientificFeatureItemEntity item
    ) {
        if (items.remove(item)) {
            item.assignFeatureVector(null);
        }
    }

    public void markCompleted(
            String newChecksum,
            Integer newSourceObservationCount
    ) {
        if (
                newSourceObservationCount == null
                        || newSourceObservationCount < 0
        ) {
            throw new IllegalArgumentException(
                    "sourceObservationCount must be non-negative"
            );
        }

        this.checksum =
                requireText(
                        newChecksum,
                        "checksum"
                );

        this.sourceObservationCount =
                newSourceObservationCount;

        this.status =
                ScientificFeatureVectorStatus.COMPLETED;
    }

    public void markFailed() {
        this.status =
                ScientificFeatureVectorStatus.FAILED;
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

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public Integer getSourceObservationCount() {
        return sourceObservationCount;
    }

    public ScientificFeatureVectorStatus getStatus() {
        return status;
    }

    public String getChecksum() {
        return checksum;
    }

    public List<ScientificFeatureItemEntity> getItems() {
        return Collections.unmodifiableList(items);
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