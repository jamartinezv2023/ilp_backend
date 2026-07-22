package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "assessment_results")
public class AssessmentScientificResultEntity {

    @Id
    @Column(length = 255)
    private String id;

    @Column(
            name = "administration_id",
            nullable = false,
            unique = true,
            length = 255
    )
    private String administrationId;

    @Column(
            name = "participant_id",
            nullable = false,
            length = 255
    )
    private String participantId;

    @Column(
            name = "assessment_code",
            nullable = false,
            length = 255
    )
    private String assessmentCode;

    @Column(
            name = "assessment_version",
            nullable = false,
            length = 255
    )
    private String assessmentVersion;

    @Column(
            name = "primary_profile",
            length = 255
    )
    private String primaryProfile;

    @Column(
            name = "scoring_algorithm_version",
            nullable = false,
            length = 255
    )
    private String scoringAlgorithmVersion;

    @Column(
            name = "interpretation_version",
            length = 255
    )
    private String interpretationVersion;

    @Column(
            name = "calculated_at",
            nullable = false
    )
    private Instant calculatedAt;

    @Column(
            name = "submitted_at",
            nullable = false
    )
    private Instant submittedAt;

    @Column(
            name = "feature_cutoff_at",
            nullable = false
    )
    private Instant featureCutoffAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @OneToMany(
            mappedBy = "result",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("dimensionCode ASC")
    private Set<AssessmentScoreItemEntity> scores =
            new LinkedHashSet<>();

    @OneToMany(
            mappedBy = "result",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("interpretationCode ASC")
    private Set<AssessmentInterpretationEntity> interpretations =
            new LinkedHashSet<>();

    protected AssessmentScientificResultEntity() {
    }

    public AssessmentScientificResultEntity(
            String id,
            String administrationId,
            String participantId,
            String assessmentCode,
            String assessmentVersion,
            String primaryProfile,
            String scoringAlgorithmVersion,
            String interpretationVersion,
            Instant calculatedAt,
            Instant submittedAt,
            Instant featureCutoffAt
    ) {
        this.id = id;
        this.administrationId = administrationId;
        this.participantId = participantId;
        this.assessmentCode = assessmentCode;
        this.assessmentVersion = assessmentVersion;
        this.primaryProfile = primaryProfile;
        this.scoringAlgorithmVersion =
                scoringAlgorithmVersion;
        this.interpretationVersion =
                interpretationVersion;
        this.calculatedAt = calculatedAt;
        this.submittedAt = submittedAt;
        this.featureCutoffAt = featureCutoffAt;
    }

    @PrePersist
    void initializeCreatedAt() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void addScore(
            AssessmentScoreItemEntity score
    ) {
        score.assignResult(this);
        scores.add(score);
    }

    public void addInterpretation(
            AssessmentInterpretationEntity interpretation
    ) {
        interpretation.assignResult(this);
        interpretations.add(interpretation);
    }

    public String getId() {
        return id;
    }

    public String getAdministrationId() {
        return administrationId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getAssessmentCode() {
        return assessmentCode;
    }

    public String getAssessmentVersion() {
        return assessmentVersion;
    }

    public String getPrimaryProfile() {
        return primaryProfile;
    }

    public String getScoringAlgorithmVersion() {
        return scoringAlgorithmVersion;
    }

    public String getInterpretationVersion() {
        return interpretationVersion;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getFeatureCutoffAt() {
        return featureCutoffAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<AssessmentScoreItemEntity> getScores() {
        return List.copyOf(scores);
    }

    public List<AssessmentInterpretationEntity>
    getInterpretations() {
        return List.copyOf(interpretations);
    }
}