package com.inclusive.adaptiveeducationservice.assessmentdefinition.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessment_definitions")
public class AssessmentDefinitionEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String assessmentType;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Integer estimatedMinutes;

    @Column(nullable = false, length = 4000)
    private String instructions;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "assessmentDefinition",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("displayOrder ASC")
    private List<AssessmentQuestionEntity> questions = new ArrayList<>();

    protected AssessmentDefinitionEntity() {
    }

    public AssessmentDefinitionEntity(
            String id,
            String code,
            String name,
            String description,
            String assessmentType,
            String version,
            Boolean active,
            Integer estimatedMinutes,
            String instructions,
            Instant createdAt
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.assessmentType = assessmentType;
        this.version = version;
        this.active = active;
        this.estimatedMinutes = estimatedMinutes;
        this.instructions = instructions;
        this.createdAt = createdAt;
    }

    public void addQuestion(AssessmentQuestionEntity question) {
        question.assignAssessmentDefinition(this);
        this.questions.add(question);
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public String getVersion() {
        return version;
    }

    public Boolean getActive() {
        return active;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public String getInstructions() {
        return instructions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<AssessmentQuestionEntity> getQuestions() {
        return List.copyOf(questions);
    }
}