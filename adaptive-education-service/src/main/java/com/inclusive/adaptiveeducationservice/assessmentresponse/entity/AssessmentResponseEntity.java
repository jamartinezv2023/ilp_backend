package com.inclusive.adaptiveeducationservice.assessmentresponse.entity;

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
@Table(name = "assessment_responses")
public class AssessmentResponseEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String assessmentCode;

    @Column(nullable = false)
    private String assessmentVersion;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant submittedAt;

    @OneToMany(
            mappedBy = "assessmentResponse",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("id ASC")
    private List<AssessmentAnswerEntity> answers = new ArrayList<>();

    protected AssessmentResponseEntity() {
    }

    public AssessmentResponseEntity(
            String id,
            String studentId,
            String assessmentCode,
            String assessmentVersion,
            String status,
            Instant submittedAt
    ) {
        this.id = id;
        this.studentId = studentId;
        this.assessmentCode = assessmentCode;
        this.assessmentVersion = assessmentVersion;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public void addAnswer(AssessmentAnswerEntity answer) {
        answer.assignAssessmentResponse(this);
        this.answers.add(answer);
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getAssessmentCode() {
        return assessmentCode;
    }

    public String getAssessmentVersion() {
        return assessmentVersion;
    }

    public String getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public List<AssessmentAnswerEntity> getAnswers() {
        return List.copyOf(answers);
    }
}