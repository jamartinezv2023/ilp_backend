package com.inclusive.adaptiveeducationservice.assessment.service;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FelderSilvermanAssessmentEngineTest {

    private final FelderSilvermanAssessmentEngine engine =
            new FelderSilvermanAssessmentEngine();

    @Test
    void shouldCalculateActiveSensingVisualSequentialProfile() {
        var answers = Collections.nCopies(44, "A");

        var result = engine.calculate(answers);

        assertThat(result.learningPreferences()).containsExactly(
                "ACTIVE",
                "SENSING",
                "VISUAL",
                "SEQUENTIAL"
        );
    }

    @Test
    void shouldCalculateReflectiveIntuitiveVerbalGlobalProfile() {
        var answers = Collections.nCopies(44, "B");

        var result = engine.calculate(answers);

        assertThat(result.learningPreferences()).containsExactly(
                "REFLECTIVE",
                "INTUITIVE",
                "VERBAL",
                "GLOBAL"
        );
    }

    @Test
    void shouldRejectInvalidAnswerSize() {
        assertThatThrownBy(() -> engine.calculate(List.of("A", "B")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("44 answers");
    }

    @Test
    void shouldRejectInvalidAnswerValue() {
        var answers = Collections.nCopies(44, "C");

        assertThatThrownBy(() -> engine.calculate(answers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A or B");
    }
}