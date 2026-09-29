package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inclusive.adaptiveeducationservice.api.assessmentresponse.AssessmentResponseController;
import com.inclusive.adaptiveeducationservice.api.assessmentscientifichistory.ParticipantAssessmentScientificHistoryController;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.AssessmentSubmissionController;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentQuestionRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.AssessmentDefinitionPersistenceMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Synthetic transport and persistence test. Definition and scoring are controlled
 * fixtures; this test does not validate an educational instrument or its scoring.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ilp_synthetic_http_e2e;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
@ActiveProfiles("test")
class SyntheticSubmissionHistoryHttpE2ETest {

    private static final String ADMIN = "SYNTHETIC-ADMIN-001";
    private static final String STUDENT = "SYNTHETIC-STUDENT-001";
    private static final String SUBJECT = "11111111-1111-1111-1111-111111111111";
    private static final String CODE = "ILP-SYNTHETIC-PHYSICS";
    private static final String VERSION = "0.0.1-test";
    private static final UUID RESEARCH_UUID = UUID.fromString(
            "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final Instant TIME = Instant.parse("2026-09-25T16:00:00Z");

    @Autowired private SubmitAssessmentService submitService;
    @Autowired private GetParticipantAssessmentScientificHistoryService historyService;
    @Autowired private AssessmentResponseService responseService;
    @Autowired private AssessmentResponseRepository responseRepository;
    @Autowired private AssessmentScientificResultRepository resultRepository;
    @Autowired private AssessmentSubmissionContextRepository contextRepository;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AssessmentDefinitionRepository definitionRepository;
    @MockBean private AssessmentDefinitionPersistenceMapper definitionMapper;
    @MockBean private StudentProfileRepository studentRepository;
    @MockBean private GenericAssessmentEngine scoringEngine;
    @MockBean private SubmitAssessmentMapper submissionMapper;
    @MockBean private ScientificParticipantIdentityPort researchIdentity;
    @MockBean private ControlledAssessmentModePolicy modePolicy;

    private MockMvc mvc;
    private SubmitAssessmentRequest request;

    @BeforeEach
    void prepare() {
        reset(definitionRepository, definitionMapper, studentRepository,
                scoringEngine, submissionMapper, researchIdentity);
        contextRepository.deleteAll();
        resultRepository.deleteAll();
        responseRepository.deleteAll();

        mvc = MockMvcBuilders.standaloneSetup(
                new AssessmentSubmissionController(submitService),
                new AssessmentResponseController(responseService),
                new ParticipantAssessmentScientificHistoryController(historyService)
        ).build();

        var answer = new SubmitAssessmentQuestionRequest(
                "PHYSICS-Q1", List.of("PHYSICS-Q1-B"), Map.of(), null, null);
        Map<String, String> context = Map.of(
                "source", "SYNTHETIC_HTTP_E2E",
                "language", "es-CO",
                "translationVersion", "0.0.1-test",
                "fieldworkPhase", "TEST_ONLY");
        request = new SubmitAssessmentRequest(ADMIN, STUDENT, RESEARCH_UUID,
                CODE, VERSION, List.of(answer), context, TIME);

        var option = new AssessmentOption("PHYSICS-Q1-B", "PHYSICS-Q1-B",
                "Respuesta ficticia B", "SYNTHETIC_SCORE", null, 1.0, 1);
        var question = new AssessmentQuestion("PHYSICS-Q1", "PHYSICS-Q1",
                "Pregunta ficticia de Física", null,
                AssessmentQuestionType.SINGLE_CHOICE, true, 1, List.of(option));
        var definition = mock(AssessmentDefinition.class);
        var definitionEntity = mock(AssessmentDefinitionEntity.class);
        when(definitionRepository.findByCodeAndActiveTrue(CODE))
                .thenReturn(Optional.of(definitionEntity));
        when(definitionEntity.getVersion()).thenReturn(VERSION);
        when(definitionMapper.toDomain(definitionEntity)).thenReturn(definition);
        when(definition.questions()).thenReturn(List.of(question));

        var submission = new AssessmentSubmission(ADMIN, STUDENT, CODE,
                VERSION, List.of(new AssessmentResponse("PHYSICS-Q1",
                List.of("PHYSICS-Q1-B"), Map.of(), null, null)), context, TIME);
        when(submissionMapper.toDomain(any(SubmitAssessmentRequest.class)))
                .thenReturn(submission);
        when(scoringEngine.evaluate(definition, submission)).thenReturn(
                new AssessmentResult(ADMIN, STUDENT, CODE, VERSION,
                        "SYNTHETIC_FEEDBACK", Map.of("SYNTHETIC_SCORE", 1.0),
                        Map.of("FEEDBACK", "Ejemplo sin interpretación científica"),
                        List.of(), "SYNTHETIC_SCORING_TEST", TIME.plusSeconds(1)));

        when(studentRepository.existsById(STUDENT)).thenReturn(true);
        when(researchIdentity.hasActiveResearchConsent(RESEARCH_UUID)).thenReturn(true);
        when(researchIdentity.resolveResearchSubjectId(RESEARCH_UUID))
                .thenReturn(Optional.of(SUBJECT));
    }

    @Test
    void postPersistsAndBothHistoriesRecoverSameAttemptThenRejectDuplicate()
            throws Exception {
        String body = objectMapper.writeValueAsString(request);

        mvc.perform(post("/api/v1/assessment-submissions")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.administrationId").value(ADMIN))
                .andExpect(jsonPath("$.assessmentVersion").value(VERSION))
                .andExpect(jsonPath("$.persistedAnswerCount").value(1));

        mvc.perform(get("/api/v1/assessment-responses/{id}", ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ADMIN))
                .andExpect(jsonPath("$.studentId").value(STUDENT))
                .andExpect(jsonPath("$.assessmentCode").value(CODE))
                .andExpect(jsonPath("$.assessmentVersion").value(VERSION))
                .andExpect(jsonPath("$.submittedAt").exists())
                .andExpect(jsonPath("$.answers[0].questionId").value("PHYSICS-Q1"))
                .andExpect(jsonPath("$.answers[0].optionId").value("PHYSICS-Q1-B"));

        mvc.perform(get("/api/v1/participants/{id}/assessment-scientific-history", SUBJECT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantId").value(SUBJECT))
                .andExpect(jsonPath("$.totalObservations").value(1))
                .andExpect(jsonPath("$.firstSubmittedAt").exists())
                .andExpect(jsonPath("$.lastSubmittedAt").exists())
                .andExpect(jsonPath("$.observations[0].administrationId").value(ADMIN))
                .andExpect(jsonPath("$.observations[0].participantId").value(SUBJECT))
                .andExpect(jsonPath("$.observations[0].assessmentCode").value(CODE))
                .andExpect(jsonPath("$.observations[0].assessmentVersion").value(VERSION))
                .andExpect(jsonPath("$.observations[0].submittedAt").exists())
                .andExpect(jsonPath("$.observations[0].scoringAlgorithmVersion")
                        .value("SYNTHETIC_SCORING_TEST"))
                .andExpect(jsonPath("$.observations[0].context.source")
                        .value("SYNTHETIC_HTTP_E2E"))
                .andExpect(jsonPath("$.observations[0].context.fieldworkPhase")
                        .value("TEST_ONLY"))
                .andExpect(jsonPath("$.observations[0].context.language").value("es-CO"))
                .andExpect(jsonPath("$.observations[0].context.completeContext.translationVersion")
                        .value("0.0.1-test"));

        mvc.perform(post("/api/v1/assessment-submissions")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/v1/participants/{id}/assessment-scientific-history", SUBJECT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalObservations").value(1));

        assertThat(responseRepository.findById(ADMIN)).isPresent();
        assertThat(responseRepository.findById(ADMIN).orElseThrow().getSubmittedAt())
                .isEqualTo(TIME);
        assertThat(resultRepository.findByAdministrationId(ADMIN)).isPresent();
        assertThat(resultRepository.findByAdministrationId(ADMIN).orElseThrow().getSubmittedAt())
                .isEqualTo(TIME);
        assertThat(contextRepository.findByAdministrationId(ADMIN)).isPresent();
        var recovered = historyService.getByParticipantId(SUBJECT);
        assertThat(recovered.firstSubmittedAt()).isEqualTo(TIME);
        assertThat(recovered.lastSubmittedAt()).isEqualTo(TIME);
        assertThat(recovered.observations().get(0).submittedAt()).isEqualTo(TIME);
    }

    @Test
    void missingActiveResearchConsentRejectsBeforeAnyWrite() throws Exception {
        when(researchIdentity.hasActiveResearchConsent(RESEARCH_UUID)).thenReturn(false);
        mvc.perform(post("/api/v1/assessment-submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        assertThat(responseRepository.findById(ADMIN)).isEmpty();
        assertThat(resultRepository.findByAdministrationId(ADMIN)).isEmpty();
        assertThat(contextRepository.findByAdministrationId(ADMIN)).isEmpty();
    }
}
