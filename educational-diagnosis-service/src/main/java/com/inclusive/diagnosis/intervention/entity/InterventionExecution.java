package com.inclusive.diagnosis.intervention.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "intervention_execution")
@Getter
@Setter
public class InterventionExecution {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID studentProfileId;

    @Column(nullable = false)
    private String interventionCategory;

    @Column(nullable = false, length = 4000)
    private String interventionDescription;

    @Column(nullable = false)
    private String responsibleTeacher;

    @Column(nullable = false)
    private Boolean engagementImproved;

    @Column(nullable = false)
    private Double progressScore;

    @Column(length = 4000)
    private String teacherObservations;

    @Column(nullable = false)
    private LocalDateTime executedAt;
}
