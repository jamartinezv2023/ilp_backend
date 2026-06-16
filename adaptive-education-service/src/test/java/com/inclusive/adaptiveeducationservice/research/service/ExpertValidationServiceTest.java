package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpertValidationServiceTest {

    private final ExpertValidationService service = new ExpertValidationService();

    @Test
    void shouldGenerateExpertValidationPreview() {
        var response = service.generateExpertValidationPreview();

        assertThat(response.expertValidationStatus()).contains("EXPERT_REVIEW");
        assertThat(response.pedagogicalExpertiseRequired()).contains("EDUCATIONAL");
        assertThat(response.inclusionExpertiseRequired()).contains("INCLUSIVE");
        assertThat(response.aiEthicsExpertiseRequired()).contains("AI");
        assertThat(response.technologyExpertiseRequired()).contains("TECHNOLOGY");
        assertThat(response.validationCriteria()).isNotEmpty();
        assertThat(response.recommendedAction()).contains("expert validation rubric");
    }
}
