package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResearchInstrumentsValidationServiceTest {

    private final ResearchInstrumentsValidationService service =
            new ResearchInstrumentsValidationService();

    @Test
    void shouldGenerateInstrumentsValidationPreview() {
        var response = service.generateInstrumentsValidationPreview();

        assertThat(response.instrumentsValidationStatus()).contains("INSTRUMENT");
        assertThat(response.teacherQuestionnaireStatus()).contains("TEACHER");
        assertThat(response.expertRubricStatus()).contains("EXPERT");
        assertThat(response.interviewGuideStatus()).contains("INTERVIEW");
        assertThat(response.validationDimensions()).isNotEmpty();
        assertThat(response.recommendedAction()).contains("teacher survey");
    }
}
