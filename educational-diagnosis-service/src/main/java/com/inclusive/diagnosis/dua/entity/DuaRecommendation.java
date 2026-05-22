package com.inclusive.diagnosis.dua.entity;

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
@Table(name = "dua_recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DuaRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID studentProfileId;

    @Column(nullable = false)
    private String duaPrinciple;

    @Column(nullable = false)
    private String recommendationCategory;

    @Column(nullable = false, length = 4000)
    private String recommendationText;

    private String accessibilitySupport;

    private String assistiveTechnologySuggestion;

    private String implementationGuidance;

    private Double priorityScore;

    private LocalDateTime generatedAt;
}
