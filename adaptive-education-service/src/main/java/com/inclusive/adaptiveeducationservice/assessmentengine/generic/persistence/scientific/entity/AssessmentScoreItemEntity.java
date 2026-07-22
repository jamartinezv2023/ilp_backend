package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "assessment_score_items")
public class AssessmentScoreItemEntity {

    @Id
    @Column(length = 255)
    private String id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "result_id",
            nullable = false
    )
    private AssessmentScientificResultEntity result;

    @Column(
            name = "administration_id",
            nullable = false,
            length = 255
    )
    private String administrationId;

    @Column(
            name = "dimension_code",
            nullable = false,
            length = 255
    )
    private String dimensionCode;

    @Column(
            name = "numeric_value",
            nullable = false
    )
    private Double numericValue;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    protected AssessmentScoreItemEntity() {
    }

    public AssessmentScoreItemEntity(
            String id,
            String administrationId,
            String dimensionCode,
            Double numericValue
    ) {
        this.id = id;
        this.administrationId = administrationId;
        this.dimensionCode = dimensionCode;
        this.numericValue = numericValue;
    }

    @PrePersist
    void initializeCreatedAt() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    void assignResult(
            AssessmentScientificResultEntity result
    ) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public String getResultId() {
        return result == null
                ? null
                : result.getId();
    }

    public String getAdministrationId() {
        return administrationId;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}