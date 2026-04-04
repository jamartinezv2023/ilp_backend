package com.inclusive.adaptiveeducationservice.domain.adaptation;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Owned by adaptive-education-service.
 * Stores the recommendation produced for a student.
 */
@Entity
@Table(name = "learning_recommendations")
public class LearningRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String tenantId;

    /**
     * Assessment type that triggered this recommendation (Kolb, Felder, Kuder...)
     */
    @Column(nullable = false, length = 80)
    private String assessmentType;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false, length = 255)
    private String result;

    /**
     * Recommendation detail (MVP: text/json-like string).
     */
    @Column(nullable = false, length = 2000)
    private String recommendedItems;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected LearningRecommendation() {}

    public LearningRecommendation(
            Long studentId,
            String tenantId,
            String assessmentType,
            Double score,
            String result,
            String recommendedItems,
            String reason
    ) {
        this.studentId = studentId;
        this.tenantId = tenantId;
        this.assessmentType = assessmentType;
        this.score = score;
        this.result = result;
        this.recommendedItems = recommendedItems;
        this.reason = reason;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public String getTenantId() { return tenantId; }
    public String getAssessmentType() { return assessmentType; }
    public Double getScore() { return score; }
    public String getResult() { return result; }
    public String getRecommendedItems() { return recommendedItems; }
    public String getReason() { return reason; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
