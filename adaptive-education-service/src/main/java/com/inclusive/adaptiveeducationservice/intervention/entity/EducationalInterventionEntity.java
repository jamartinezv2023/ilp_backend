package com.inclusive.adaptiveeducationservice.intervention.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "educational_interventions")
public class EducationalInterventionEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String responsibleRole;

    @Column(nullable = false)
    private String interventionType;

    @Column(nullable = false, length = 1500)
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected EducationalInterventionEntity() {
    }

    public EducationalInterventionEntity(
            String id,
            String studentId,
            String title,
            String responsibleRole,
            String interventionType,
            String description,
            String status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.studentId = studentId;
        this.title = title;
        this.responsibleRole = responsibleRole;
        this.interventionType = interventionType;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateStatus(String status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }

    public String getStudentId() { return studentId; }

    public String getTitle() { return title; }

    public String getResponsibleRole() { return responsibleRole; }

    public String getInterventionType() { return interventionType; }

    public String getDescription() { return description; }

    public String getStatus() { return status; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
}