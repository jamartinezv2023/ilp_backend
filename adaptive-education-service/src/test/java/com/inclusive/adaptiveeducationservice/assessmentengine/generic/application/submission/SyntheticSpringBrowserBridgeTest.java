package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.AssessmentDefinitionPersistenceMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentScientificResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.repository.AssessmentSubmissionContextRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Starts the actual embedded HTTP server, controllers, mapper, services and H2
 * persistence adapters while Playwright exercises the browser on loopback.
 * Definition, scoring, student and consent are synthetic test fixtures.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.address=127.0.0.1",
        "server.port=4187",
        "spring.datasource.url=jdbc:h2:mem:ilp_browser_spring_e2e;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false",
        "app.cors.allowed-origin-patterns=http://127.0.0.1:5177"
})
@ActiveProfiles("test")
class SyntheticSpringBrowserBridgeTest {
    private static final String CODE = "ILP-SYNTHETIC-PHYSICS";
    private static final String VERSION = "0.0.1-test";
    private static final String STUDENT = "SYNTHETIC-STUDENT-001";
    private static final String SUBJECT = "11111111-1111-1111-1111-111111111111";
    private static final UUID RESEARCH_UUID = UUID.fromString(
            "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Autowired private AssessmentResponseRepository responseRepository;
    @Autowired private AssessmentScientificResultRepository resultRepository;
    @Autowired private AssessmentSubmissionContextRepository contextRepository;

    @MockBean private AssessmentDefinitionRepository definitionRepository;
    @MockBean private AssessmentDefinitionPersistenceMapper definitionMapper;
    @MockBean private StudentProfileRepository studentRepository;
    @MockBean private GenericAssessmentEngine scoringEngine;
    @MockBean private ScientificParticipantIdentityPort researchIdentity;
    @MockBean private ControlledAssessmentModePolicy modePolicy;

    @Test
    @Timeout(180)
    void browserPostsAndReadsThroughRealSpringHttpAndJpa() throws Exception {
        contextRepository.deleteAll();
        resultRepository.deleteAll();
        responseRepository.deleteAll();

        var optionA = new AssessmentOption("PHYSICS-Q1-A", "PHYSICS-Q1-A",
                "Opción ficticia A", "SYNTHETIC_SCORE", null, 1.0, 1);
        var optionB = new AssessmentOption("PHYSICS-Q1-B", "PHYSICS-Q1-B",
                "Opción ficticia B", "SYNTHETIC_SCORE", null, 1.0, 2);
        var question = new AssessmentQuestion("PHYSICS-Q1", "PHYSICS-Q1",
                "Pregunta ficticia de Física", null,
                AssessmentQuestionType.SINGLE_CHOICE, true, 1,
                List.of(optionA, optionB));
        var definition = mock(AssessmentDefinition.class);
        var entity = mock(AssessmentDefinitionEntity.class);
        when(definitionRepository.findByCodeAndActiveTrue(CODE))
                .thenReturn(Optional.of(entity));
        when(entity.getVersion()).thenReturn(VERSION);
        when(definitionMapper.toDomain(entity)).thenReturn(definition);
        when(definition.questions()).thenReturn(List.of(question));

        when(studentRepository.existsById(STUDENT)).thenReturn(true);
        when(researchIdentity.hasActiveResearchConsent(RESEARCH_UUID)).thenReturn(true);
        when(researchIdentity.resolveResearchSubjectId(RESEARCH_UUID))
                .thenReturn(Optional.of(SUBJECT));
        when(scoringEngine.evaluate(eq(definition), any(AssessmentSubmission.class)))
                .thenAnswer(call -> {
                    AssessmentSubmission submission = call.getArgument(1);
                    return new AssessmentResult(submission.administrationId(),
                            submission.participantId(), submission.assessmentCode(),
                            submission.assessmentVersion(), "SYNTHETIC_FEEDBACK",
                            Map.of("SYNTHETIC_SCORE", 1.0),
                            Map.of("FEEDBACK", "Solo prueba de transporte"),
                            List.of(), "SYNTHETIC_SCORING_TEST", Instant.now());
                });

        Path ready = Path.of(requireEnv("ILP_E2E_READY_FILE"));
        Path release = Path.of(requireEnv("ILP_E2E_RELEASE_FILE"));
        Path attempt = Path.of(requireEnv("ILP_E2E_ATTEMPT_FILE"));
        Files.writeString(ready, "READY");
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(145);
        while (!Files.exists(release) && System.nanoTime() < deadline) {
            Thread.sleep(100);
        }
        assertThat(Files.exists(release)).as("Browser runner released test").isTrue();
        assertThat(Files.exists(attempt)).as("Browser recorded confirmed attempt").isTrue();
        String administrationId = Files.readString(attempt).trim();
        assertThat(administrationId).matches("SYNTHETIC-[0-9a-f-]{36}");
        assertThat(responseRepository.findById(administrationId)).isPresent();
        assertThat(resultRepository.findByAdministrationId(administrationId)).isPresent();
        assertThat(contextRepository.findByAdministrationId(administrationId)).isPresent();
        assertThat(responseRepository.count()).isEqualTo(1);
        assertThat(resultRepository.count()).isEqualTo(1);
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required test path missing: " + name);
        }
        return value;
    }
}
