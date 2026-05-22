package com.inclusive.diagnosis.indicator.entity;

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
@Table(name = "learning_indicators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID studentProfileId;

    @Column(nullable = false)
    private String indicatorCode;

    @Column(nullable = false)
    private String indicatorCategory;

    @Column(nullable = false)
    private Double indicatorValue;

    private String interpretation;

    private String pedagogicalRecommendation;

    private LocalDateTime calculatedAt;
}
