package com.inclusive.adaptiveeducationservice.assessmentdefinition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "assessment_options")
public class AssessmentOptionEntity {

    @Id
    private String id;

    @ManyToOne(optional = false)
    private AssessmentQuestionEntity question;

    @Column(nullable = false)
    private String label;

    @Column(
            name = "option_value",
            nullable = false
    )
    private String value;

    @Column(nullable = false)
    private Integer weight;

    @Column(nullable = false)
    private Integer displayOrder;

    protected AssessmentOptionEntity() {
    }

    public AssessmentOptionEntity(
            String id,
            String label,
            String value,
            Integer weight,
            Integer displayOrder
    ) {
        this.id = id;
        this.label = label;
        this.value = value;
        this.weight = weight;
        this.displayOrder = displayOrder;
    }

    void assignQuestion(AssessmentQuestionEntity question) {
        this.question = question;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}