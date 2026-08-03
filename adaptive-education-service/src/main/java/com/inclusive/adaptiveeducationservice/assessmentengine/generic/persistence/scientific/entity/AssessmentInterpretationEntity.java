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
@Table(name = "assessment_interpretations")
public class AssessmentInterpretationEntity {

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
            name = "interpretation_code",
            nullable = false,
            length = 255
    )
    private String interpretationCode;

    @Column(name = "interpretation_text")
    private String interpretationText;

    @Column(
            name = "interpretation_version",
            length = 255
    )
    private String interpretationVersion;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    protected AssessmentInterpretationEntity() {
    }

    public AssessmentInterpretationEntity(
            String id,
            String administrationId,
            String interpretationCode,
            String interpretationText,
            String interpretationVersion
    ) {
        this.id = id;
        this.administrationId = administrationId;
        this.interpretationCode = interpretationCode;
        this.interpretationText = interpretationText;
        this.interpretationVersion = interpretationVersion;
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

    public String getInterpretationCode() {
        return interpretationCode;
    }

    public String getInterpretationText() {
        return interpretationText;
    }

    public String getInterpretationVersion() {
        return interpretationVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}