package com.inclusive.adaptiveeducationservice.assessment.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KolbAssessmentEngineTest {

    private final KolbAssessmentEngine engine = new KolbAssessmentEngine();

    @Test
    void shouldCalculateDivergentLearningStyle() {
        var answers = List.of(
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1
        );

        var result = engine.calculate(answers);

        assertThat(result.learningStyle()).isEqualTo("DIVERGENT");
        assertThat(result.scoreCE()).isEqualTo(48);
        assertThat(result.scoreRO()).isEqualTo(48);
    }

    @Test
    void shouldRejectInvalidAnswerSize() {
        assertThatThrownBy(() -> engine.calculate(List.of(1, 2, 3)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("48 answers");
    }

    @Test
    void shouldRejectInvalidAnswerValue() {
        var answers = List.of(
                5, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1,
                4, 4, 1, 1
        );

        assertThatThrownBy(() -> engine.calculate(answers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 4");
    }
}