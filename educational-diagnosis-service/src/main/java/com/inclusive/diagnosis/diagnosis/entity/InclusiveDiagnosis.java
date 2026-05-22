package com.inclusive.diagnosis.diagnosis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inclusive_diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InclusiveDiagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID studentProfileId;

    @Column(nullable = false)
    private String diagnosisCategory;

    @Column(nullable = false, length = 4000)
    private String diagnosisSummary;

    private String identifiedBarriers;

    private String learningStrengths;

    private String supportNeeds;

    private String recommendedInterventions;

    private String dueAlignment;

    private Double confidenceScore;

    private LocalDateTime generatedAt;
}
