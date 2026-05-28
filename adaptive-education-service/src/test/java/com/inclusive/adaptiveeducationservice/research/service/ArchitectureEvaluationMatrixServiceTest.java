package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureEvaluationMatrixServiceTest {

    private final ArchitectureEvaluationMatrixService service =
            new ArchitectureEvaluationMatrixService();

    @Test
    void shouldGenerateArchitectureEvaluationMatrixPreview() {
        var response = service.generateEvaluationMatrixPreview();

        assertThat(response.evaluationMatrixStatus()).contains("EVALUATOR");
        assertThat(response.rubricAlignmentLevel()).contains("RUBRIC");
        assertThat(response.evaluatedCriteria()).isNotEmpty();
        assertThat(response.currentEvidence()).isNotEmpty();
        assertThat(response.pendingEvidence()).isNotEmpty();
        assertThat(response.recommendedEvaluatorAction()).contains("architecture evaluation");
    }
}
