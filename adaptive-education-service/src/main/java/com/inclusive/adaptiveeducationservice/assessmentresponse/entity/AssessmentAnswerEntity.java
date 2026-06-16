package com.inclusive.adaptiveeducationservice.assessmentresponse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "assessment_answers")
public class AssessmentAnswerEntity {

    @Id
    private String id;

    @ManyToOne(optional = false)
    private AssessmentResponseEntity assessmentResponse;

    @Column(nullable = false)
    private String questionId;

    @Column(nullable = false)
    private String optionId;

    @Column(nullable = false)
    private String dimension;

    @Column(
            name = "answer_value",
            nullable = false
    )
    private String value;

    @Column(nullable = false)
    private Integer score;

    protected AssessmentAnswerEntity() {
    }

    public AssessmentAnswerEntity(
            String id,
            String questionId,
            String optionId,
            String dimension,
            String value,
            Integer score
    ) {
        this.id = id;
        this.questionId = questionId;
        this.optionId = optionId;
        this.dimension = dimension;
        this.value = value;
        this.score = score;
    }

    void assignAssessmentResponse(AssessmentResponseEntity assessmentResponse) {
        this.assessmentResponse = assessmentResponse;
    }

    public String getId() {
        return id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getOptionId() {
        return optionId;
    }

    public String getDimension() {
        return dimension;
    }

    public String getValue() {
        return value;
    }

    public Integer getScore() {
        return score;
    }
}