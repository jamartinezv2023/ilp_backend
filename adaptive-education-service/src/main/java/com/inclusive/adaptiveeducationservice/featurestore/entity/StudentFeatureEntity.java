package com.inclusive.adaptiveeducationservice.featurestore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "student_features")
public class StudentFeatureEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private Instant featureDate;

    @Column(nullable = false)
    private Integer kolbCe;

    @Column(nullable = false)
    private Integer kolbRo;

    @Column(nullable = false)
    private Integer kolbAc;

    @Column(nullable = false)
    private Integer kolbAe;

    @Column(nullable = false)
    private String kolbStyle;

    protected StudentFeatureEntity() {
    }

    public StudentFeatureEntity(
            String id,
            String studentId,
            Instant featureDate,
            Integer kolbCe,
            Integer kolbRo,
            Integer kolbAc,
            Integer kolbAe,
            String kolbStyle
    ) {
        this.id = id;
        this.studentId = studentId;
        this.featureDate = featureDate;
        this.kolbCe = kolbCe;
        this.kolbRo = kolbRo;
        this.kolbAc = kolbAc;
        this.kolbAe = kolbAe;
        this.kolbStyle = kolbStyle;
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public Instant getFeatureDate() {
        return featureDate;
    }

    public Integer getKolbCe() {
        return kolbCe;
    }

    public Integer getKolbRo() {
        return kolbRo;
    }

    public Integer getKolbAc() {
        return kolbAc;
    }

    public Integer getKolbAe() {
        return kolbAe;
    }

    public String getKolbStyle() {
        return kolbStyle;
    }
}