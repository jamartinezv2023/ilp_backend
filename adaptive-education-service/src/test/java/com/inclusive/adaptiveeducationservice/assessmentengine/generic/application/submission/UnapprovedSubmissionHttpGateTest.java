package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.AssessmentSubmissionController;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentQuestionRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ilp_unapproved_gate;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("test")
class UnapprovedSubmissionHttpGateTest {
    @Autowired private SubmitAssessmentService service;
    @Autowired private AssessmentResponseRepository responseRepository;
    @Autowired private AssessmentScientificResultRepository resultRepository;
    @Autowired private AssessmentSubmissionContextRepository contextRepository;
    @Autowired private ObjectMapper mapper;
    @MockBean private AssessmentDefinitionRepository definitionRepository;
    @MockBean private StudentProfileRepository studentRepository;
    @MockBean private ScientificParticipantIdentityPort participantIdentity;
    @MockBean private GenericAssessmentEngine scoringEngine;

    @Test
    void activeDefinitionCannotReplaceFieldworkApproval() throws Exception {
        var request = new SubmitAssessmentRequest(
                "ADMIN-NO-APPROVAL-001", "ST-NO-APPROVAL-001", null,
                "KOLB_V1", "1.0",
                List.of(new SubmitAssessmentQuestionRequest(
                        "Q1", List.of("Q1-A"), Map.of(), null, null)),
                Map.of("executionMode", "FIELDWORK"),
                Instant.parse("2026-09-26T00:00:00Z")
        );
        var mvc = MockMvcBuilders.standaloneSetup(
                new AssessmentSubmissionController(service)).build();
        mvc.perform(post("/api/v1/assessment-submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(request)))
                .andExpect(status().isForbidden());

        assertThat(responseRepository.findById("ADMIN-NO-APPROVAL-001")).isEmpty();
        assertThat(resultRepository.findByAdministrationId("ADMIN-NO-APPROVAL-001")).isEmpty();
        assertThat(contextRepository.findByAdministrationId("ADMIN-NO-APPROVAL-001")).isEmpty();
    }
}
