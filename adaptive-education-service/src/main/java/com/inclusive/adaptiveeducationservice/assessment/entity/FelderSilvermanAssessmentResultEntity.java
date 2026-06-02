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
@Table(name = "felder_silverman_assessment_results")
public class FelderSilvermanAssessmentResultEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private Integer activeReflectiveScore;

    @Column(nullable = false)
    private Integer sensingIntuitiveScore;

    @Column(nullable = false)
    private Integer visualVerbalScore;

    @Column(nullable = false)
    private Integer sequentialGlobalScore;

    @Column(nullable = false)
    private String dominantProfile;

    @Column(nullable = false)
    private String instrumentVersion;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "felder_silverman_answers",
            joinColumns = @JoinColumn(name = "assessment_id")
    )
    @Column(name = "answer_value", nullable = false)
    private List<String> answers = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "felder_silverman_preferences",
            joinColumns = @JoinColumn(name = "assessment_id")
    )
    @Column(name = "preference", nullable = false)
    private List<String> learningPreferences = new ArrayList<>();

    protected FelderSilvermanAssessmentResultEntity() {
    }

    public FelderSilvermanAssessmentResultEntity(
            String id,
            String studentId,
            Integer activeReflectiveScore,
            Integer sensingIntuitiveScore,
            Integer visualVerbalScore,
            Integer sequentialGlobalScore,
            String dominantProfile,
            String instrumentVersion,
            Instant createdAt,
            List<String> answers,
            List<String> learningPreferences
    ) {
        this.id = id;
        this.studentId = studentId;
        this.activeReflectiveScore = activeReflectiveScore;
        this.sensingIntuitiveScore = sensingIntuitiveScore;
        this.visualVerbalScore = visualVerbalScore;
        this.sequentialGlobalScore = sequentialGlobalScore;
        this.dominantProfile = dominantProfile;
        this.instrumentVersion = instrumentVersion;
        this.createdAt = createdAt;
        this.answers = new ArrayList<>(answers);
        this.learningPreferences = new ArrayList<>(learningPreferences);
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public Integer getActiveReflectiveScore() {
        return activeReflectiveScore;
    }

    public Integer getSensingIntuitiveScore() {
        return sensingIntuitiveScore;
    }

    public Integer getVisualVerbalScore() {
        return visualVerbalScore;
    }

    public Integer getSequentialGlobalScore() {
        return sequentialGlobalScore;
    }

    public String getDominantProfile() {
        return dominantProfile;
    }

    public String getInstrumentVersion() {
        return instrumentVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<String> getLearningPreferences() {
        return List.copyOf(learningPreferences);
    }
}