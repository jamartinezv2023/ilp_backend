package com.inclusive.adaptiveeducationservice.assessmentdefinition.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessment_questions")
public class AssessmentQuestionEntity {

    @Id
    private String id;

    @ManyToOne(optional = false)
    private AssessmentDefinitionEntity assessmentDefinition;

    @Column(nullable = false)
    private Integer questionNumber;

    @Column(nullable = false, length = 2000)
    private String text;

    @Column(nullable = false)
    private String dimension;

    @Column(nullable = false, length = 2000)
    private String helpText;

    @Column(nullable = false)
    private Boolean required;

    @Column(nullable = false)
    private String questionType;

    @Column(nullable = false)
    private Integer displayOrder;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("displayOrder ASC")
    private List<AssessmentOptionEntity> options = new ArrayList<>();

    protected AssessmentQuestionEntity() {
    }

    public AssessmentQuestionEntity(
            String id,
            Integer questionNumber,
            String text,
            String dimension,
            String helpText,
            Boolean required,
            String questionType,
            Integer displayOrder
    ) {
        this.id = id;
        this.questionNumber = questionNumber;
        this.text = text;
        this.dimension = dimension;
        this.helpText = helpText;
        this.required = required;
        this.questionType = questionType;
        this.displayOrder = displayOrder;
    }

    void assignAssessmentDefinition(
            AssessmentDefinitionEntity assessmentDefinition
    ) {
        this.assessmentDefinition = assessmentDefinition;
    }

    public void addOption(AssessmentOptionEntity option) {
        option.assignQuestion(this);
        this.options.add(option);
    }

    public String getId() {
        return id;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public String getText() {
        return text;
    }

    public String getDimension() {
        return dimension;
    }

    public String getHelpText() {
        return helpText;
    }

    public Boolean getRequired() {
        return required;
    }

    public String getQuestionType() {
        return questionType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public List<AssessmentOptionEntity> getOptions() {
        return List.copyOf(options);
    }
}