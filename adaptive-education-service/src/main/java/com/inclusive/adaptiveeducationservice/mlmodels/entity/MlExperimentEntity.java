package com.inclusive.adaptiveeducationservice.mlmodels.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ml_experiments")
public class MlExperimentEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private Integer totalRows;

    @Column(nullable = false)
    private Integer trainRows;

    @Column(nullable = false)
    private Integer validationRows;

    @Column(nullable = false)
    private Integer testRows;

    @Column(nullable = false)
    private Double accuracy;

    @Column(nullable = false)
    private Double precisionScore;

    @Column(nullable = false)
    private Double recallScore;

    @Column(nullable = false)
    private Double f1Score;

    @Column(nullable = false, length = 3000)
    private String featureImportanceJson;

    @Column(nullable = false)
    private Instant createdAt;

    protected MlExperimentEntity() {
    }

    public MlExperimentEntity(
            String id,
            String algorithm,
            String target,
            Integer totalRows,
            Integer trainRows,
            Integer validationRows,
            Integer testRows,
            Double accuracy,
            Double precisionScore,
            Double recallScore,
            Double f1Score,
            String featureImportanceJson,
            Instant createdAt
    ) {
        this.id = id;
        this.algorithm = algorithm;
        this.target = target;
        this.totalRows = totalRows;
        this.trainRows = trainRows;
        this.validationRows = validationRows;
        this.testRows = testRows;
        this.accuracy = accuracy;
        this.precisionScore = precisionScore;
        this.recallScore = recallScore;
        this.f1Score = f1Score;
        this.featureImportanceJson = featureImportanceJson;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }

    public String getAlgorithm() { return algorithm; }

    public String getTarget() { return target; }

    public Integer getTotalRows() { return totalRows; }

    public Integer getTrainRows() { return trainRows; }

    public Integer getValidationRows() { return validationRows; }

    public Integer getTestRows() { return testRows; }

    public Double getAccuracy() { return accuracy; }

    public Double getPrecisionScore() { return precisionScore; }

    public Double getRecallScore() { return recallScore; }

    public Double getF1Score() { return f1Score; }

    public String getFeatureImportanceJson() { return featureImportanceJson; }

    public Instant getCreatedAt() { return createdAt; }
}