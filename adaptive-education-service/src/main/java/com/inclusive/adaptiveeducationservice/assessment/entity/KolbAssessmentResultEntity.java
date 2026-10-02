package com.inclusive.adaptiveeducationservice.assessment.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kolb_assessment_results")
public class KolbAssessmentResultEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private Integer scoreCE;

    @Column(nullable = false)
    private Integer scoreRO;

    @Column(nullable = false)
    private Integer scoreAC;

    @Column(nullable = false)
    private Integer scoreAE;

    @Column(nullable = false)
    private String learningStyle;

    @Column(nullable = false)
    private String instrumentVersion;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "kolb_assessment_answers",
            joinColumns = @JoinColumn(name = "assessment_id")
    )
    @Column(name = "answer_value", nullable = false)
    private List<Integer> answers = new ArrayList<>();

    protected KolbAssessmentResultEntity() {
    }

    public KolbAssessmentResultEntity(
            String id,
            String studentId,
            Integer scoreCE,
            Integer scoreRO,
            Integer scoreAC,
            Integer scoreAE,
            String learningStyle,
            String instrumentVersion,
            Instant createdAt,
            List<Integer> answers
    ) {
        this.id = id;
        this.studentId = studentId;
        this.scoreCE = scoreCE;
        this.scoreRO = scoreRO;
        this.scoreAC = scoreAC;
        this.scoreAE = scoreAE;
        this.learningStyle = learningStyle;
        this.instrumentVersion = instrumentVersion;
        this.createdAt = createdAt;
        this.answers = new ArrayList<>(answers);
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public Integer getScoreCE() {
        return scoreCE;
    }

    public Integer getScoreRO() {
        return scoreRO;
    }

    public Integer getScoreAC() {
        return scoreAC;
    }

    public Integer getScoreAE() {
        return scoreAE;
    }

    public String getLearningStyle() {
        return learningStyle;
    }

    public String getInstrumentVersion() {
        return instrumentVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Integer> getAnswers() {
        return List.copyOf(answers);
    }
}