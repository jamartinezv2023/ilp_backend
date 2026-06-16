package com.inclusive.adaptiveeducationservice.adaptive.entity;

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
@Table(name = "adaptive_learning_plans")
public class AdaptiveLearningPlanEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String riskLevel;

    @Column(nullable = false)
    private String recommendedMethodology;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "adaptive_learning_plan_resources",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @Column(name = "resource", nullable = false, length = 1000)
    private List<String> recommendedResources = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "adaptive_learning_plan_pathway",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @Column(name = "pathway_step", nullable = false, length = 1000)
    private List<String> adaptivePathway = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "adaptive_learning_plan_teacher_actions",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @Column(name = "teacher_action", nullable = false, length = 1000)
    private List<String> teacherActions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "adaptive_learning_plan_inclusion_actions",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @Column(name = "inclusion_action", nullable = false, length = 1000)
    private List<String> inclusionActions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "adaptive_learning_plan_family_actions",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @Column(name = "family_action", nullable = false, length = 1000)
    private List<String> familyActions = new ArrayList<>();

    protected AdaptiveLearningPlanEntity() {
    }

    public AdaptiveLearningPlanEntity(
            String id,
            String studentId,
            String riskLevel,
            String recommendedMethodology,
            List<String> recommendedResources,
            List<String> adaptivePathway,
            List<String> teacherActions,
            List<String> inclusionActions,
            List<String> familyActions,
            Instant createdAt
    ) {
        this.id = id;
        this.studentId = studentId;
        this.riskLevel = riskLevel;
        this.recommendedMethodology = recommendedMethodology;
        this.recommendedResources = new ArrayList<>(recommendedResources);
        this.adaptivePathway = new ArrayList<>(adaptivePathway);
        this.teacherActions = new ArrayList<>(teacherActions);
        this.inclusionActions = new ArrayList<>(inclusionActions);
        this.familyActions = new ArrayList<>(familyActions);
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRecommendedMethodology() {
        return recommendedMethodology;
    }

    public List<String> getRecommendedResources() {
        return List.copyOf(recommendedResources);
    }

    public List<String> getAdaptivePathway() {
        return List.copyOf(adaptivePathway);
    }

    public List<String> getTeacherActions() {
        return List.copyOf(teacherActions);
    }

    public List<String> getInclusionActions() {
        return List.copyOf(inclusionActions);
    }

    public List<String> getFamilyActions() {
        return List.copyOf(familyActions);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}