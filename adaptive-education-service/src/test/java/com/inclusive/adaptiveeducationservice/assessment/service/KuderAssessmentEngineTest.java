package com.inclusive.adaptiveeducationservice.assessment.service;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KuderAssessmentEngineTest {

    private final KuderAssessmentEngine engine = new KuderAssessmentEngine();

    @Test
    void shouldCalculateScientificDominantArea() {
        var answers = Collections.nCopies(30, "SCIENTIFIC");

        var result = engine.calculate(answers);

        assertThat(result.dominantVocationalArea()).isEqualTo("SCIENTIFIC");
        assertThat(result.scientificScore()).isEqualTo(30);
    }

    @Test
    void shouldRejectInvalidAnswerSize() {
        assertThatThrownBy(() -> engine.calculate(List.of("SCIENTIFIC")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("30 answers");
    }

    @Test
    void shouldRejectInvalidVocationalArea() {
        var answers = Collections.nCopies(30, "UNKNOWN");

        assertThatThrownBy(() -> engine.calculate(answers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valid vocational area");
    }
}