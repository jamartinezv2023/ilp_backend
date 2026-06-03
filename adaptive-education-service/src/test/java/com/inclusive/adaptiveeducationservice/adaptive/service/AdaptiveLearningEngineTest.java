package com.inclusive.adaptiveeducationservice.adaptive.service;

import com.inclusive.adaptiveeducationservice.student.entity.StudentProfileEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdaptiveLearningEngineTest {

    private final AdaptiveLearningEngine engine = new AdaptiveLearningEngine();

    @Test
    void shouldGenerateProjectBasedLearningForDivergentVisualStudent() {
        var student = new StudentProfileEntity(
                "ST-001",
                "Mariana Gomez",
                "6A",
                11,
                "DIVERGENT",
                "SCIENTIFIC",
                "MEDIUM",
                List.of("Visual learning support"),
                List.of("Use visual organizers")
        );

        student.updateLearningPreferences(List.of("VISUAL", "ACTIVE"));

        var plan = engine.generate(student);

        assertThat(plan.recommendedMethodology())
                .isEqualTo("PROJECT_BASED_LEARNING");
        assertThat(plan.recommendedResources())
                .contains("Infographics and visual organizers");
        assertThat(plan.adaptivePathway()).isNotEmpty();
    }

    @Test
    void shouldGenerateHighRiskActionsForHighSupportStudent() {
        var student = new StudentProfileEntity(
                "ST-003",
                "Valentina Torres",
                "8A",
                13,
                "ACCOMMODATING",
                "SOCIAL",
                "HIGH",
                List.of("Additional processing time"),
                List.of("Use private formative feedback")
        );

        student.updateLearningPreferences(List.of("ACTIVE", "GLOBAL"));

        var plan = engine.generate(student);

        assertThat(plan.riskLevel())
                .isEqualTo("HIGH_EDUCATIONAL_SUPPORT_NEED");
        assertThat(plan.inclusionActions())
                .contains("Evaluate whether PIAR support planning is required.");
    }
}