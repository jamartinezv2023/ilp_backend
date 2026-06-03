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
@Table(name = "kuder_assessment_results")
public class KuderAssessmentResultEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String dominantVocationalArea;

    @Column(nullable = false)
    private Integer scientificScore;

    @Column(nullable = false)
    private Integer artisticScore;

    @Column(nullable = false)
    private Integer socialScore;

    @Column(nullable = false)
    private Integer mechanicalScore;

    @Column(nullable = false)
    private Integer administrativeScore;

    @Column(nullable = false)
    private String instrumentVersion;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "kuder_assessment_answers",
            joinColumns = @JoinColumn(name = "assessment_id")
    )
    @Column(name = "answer_value", nullable = false)
    private List<String> answers = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "kuder_top_vocational_areas",
            joinColumns = @JoinColumn(name = "assessment_id")
    )
    @Column(name = "vocational_area", nullable = false)
    private List<String> topVocationalAreas = new ArrayList<>();

    protected KuderAssessmentResultEntity() {
    }

    public KuderAssessmentResultEntity(
            String id,
            String studentId,
            String dominantVocationalArea,
            List<String> topVocationalAreas,
            Integer scientificScore,
            Integer artisticScore,
            Integer socialScore,
            Integer mechanicalScore,
            Integer administrativeScore,
            String instrumentVersion,
            Instant createdAt,
            List<String> answers
    ) {
        this.id = id;
        this.studentId = studentId;
        this.dominantVocationalArea = dominantVocationalArea;
        this.topVocationalAreas = new ArrayList<>(topVocationalAreas);
        this.scientificScore = scientificScore;
        this.artisticScore = artisticScore;
        this.socialScore = socialScore;
        this.mechanicalScore = mechanicalScore;
        this.administrativeScore = administrativeScore;
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

    public String getDominantVocationalArea() {
        return dominantVocationalArea;
    }

    public List<String> getTopVocationalAreas() {
        return List.copyOf(topVocationalAreas);
    }

    public Integer getScientificScore() {
        return scientificScore;
    }

    public Integer getArtisticScore() {
        return artisticScore;
    }

    public Integer getSocialScore() {
        return socialScore;
    }

    public Integer getMechanicalScore() {
        return mechanicalScore;
    }

    public Integer getAdministrativeScore() {
        return administrativeScore;
    }

    public String getInstrumentVersion() {
        return instrumentVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}