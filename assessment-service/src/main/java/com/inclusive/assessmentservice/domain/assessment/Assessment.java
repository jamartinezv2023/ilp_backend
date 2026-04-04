package com.inclusive.assessmentservice.domain.assessment;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String type;

    private Double score;

    private String result;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime completedAt;

    protected Assessment() {
        // JPA
    }

    public Assessment(Long studentId, String type) {
        this.studentId = Objects.requireNonNull(studentId);
        this.type = Objects.requireNonNull(type);
        this.createdAt = OffsetDateTime.now();
    }

    public void complete(Double score, String result) {
        this.score = score;
        this.result = result;
        this.completedAt = OffsetDateTime.now();
    }

    // =====================
    // Getters
    // =====================

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getType() {
        return type;
    }

    public Double getScore() {
        return score;
    }

    public String getResult() {
        return result;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }
}
