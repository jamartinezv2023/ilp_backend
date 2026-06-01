package com.inclusive.adaptiveeducationservice.education.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EducationalSupportServiceTest {

    private final EducationalSupportService service = new EducationalSupportService();

    @Test
    void shouldGenerateInstitutionalOverview() {
        var response = service.institutionalOverview();

        assertThat(response.module()).contains("Institutional");
        assertThat(response.capabilities()).isNotEmpty();
        assertThat(response.operationalStatus()).contains("READY");
    }

    @Test
    void shouldGenerateTeacherWorkspace() {
        var response = service.teacherWorkspace();

        assertThat(response.module()).contains("Teacher");
        assertThat(response.capabilities()).isNotEmpty();
        assertThat(response.nextActions()).isNotEmpty();
    }

    @Test
    void shouldGenerateStudentSupport() {
        var response = service.studentSupport();

        assertThat(response.module()).contains("Student");
        assertThat(response.purpose()).contains("educational support");
    }

    @Test
    void shouldGenerateInclusionPiar() {
        var response = service.inclusionPiar();

        assertThat(response.module()).contains("PIAR");
        assertThat(response.capabilities()).contains("Reasonable adjustment tracking");
    }

    @Test
    void shouldGenerateFamilyEngagement() {
        var response = service.familyEngagement();

        assertThat(response.module()).contains("Family");
        assertThat(response.audience()).contains("Families");
    }
}
