package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentQuestionRequest;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.AssessmentDefinitionPersistenceMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificObservationPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificObservationConsentEligibilityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ControlledDemoIsolationTest {

    private final AssessmentResponseRepository responses = mock(AssessmentResponseRepository.class);
    private final AssessmentDefinitionRepository definitions = mock(AssessmentDefinitionRepository.class);
    private final AssessmentDefinitionPersistenceMapper mapper = mock(AssessmentDefinitionPersistenceMapper.class);
    private final StudentProfileRepository students = mock(StudentProfileRepository.class);
    private final GenericAssessmentEngine engine = mock(GenericAssessmentEngine.class);
    private final SubmitAssessmentMapper submissions = mock(SubmitAssessmentMapper.class);
    private final AssessmentScientificObservationPort observations = mock(AssessmentScientificObservationPort.class);
    private final ScientificObservationConsentEligibilityPort consent = mock(ScientificObservationConsentEligibilityPort.class);
    private final ScientificParticipantIdentityPort identities = mock(ScientificParticipantIdentityPort.class);

    private SubmitAssessmentService service() {
        return new SubmitAssessmentService(
                responses, definitions, mapper, students, engine, submissions,
                observations, consent, identities, new ControlledAssessmentModePolicy()
        );
    }

    private SubmitAssessmentRequest request(String mode, String participantId) {
        return new SubmitAssessmentRequest(
                "DEMO-ADMIN-001", participantId, null, "ILP-MEA",
                "0.1.0-candidate", List.of(new SubmitAssessmentQuestionRequest(
                        "MEA-Q01", List.of("MEA-Q01-O3"), Map.of(), null, null)),
                Map.of("executionMode", mode,
                        "language", "es-CO",
                        "translationVersion", "0.1.0-original-es-CO"),
                Instant.parse("2026-09-25T00:00:00Z")
        );
    }

    @Test
    void syntheticDemoCalculatesButNeverWritesOrLooksUpRealIdentity() {
        var request = request("DEMO", "DEMO-001");
        var entity = mock(AssessmentDefinitionEntity.class);
        var definition = mock(AssessmentDefinition.class);
        var submission = mock(AssessmentSubmission.class);
        var result = new AssessmentResult(
                "DEMO-ADMIN-001", "DEMO-001", "ILP-MEA", "0.1.0-candidate",
                "CANDIDATE", Map.of("DIM", 3.0), Map.of(), List.of(),
                "CANDIDATE_SCORING_V1", Instant.parse("2026-09-25T00:00:01Z")
        );

        when(definitions.findByCodeAndActiveTrue("ILP-MEA")).thenReturn(Optional.of(entity));
        when(entity.getVersion()).thenReturn("0.1.0-candidate");
        when(mapper.toDomain(entity)).thenReturn(definition);
        when(submissions.toDomain(request)).thenReturn(submission);
        when(engine.evaluate(definition, submission)).thenReturn(result);

        var outcome = service().submit(request);

        assertThat(outcome.status()).isEqualTo("DEMO_COMPLETED_NOT_PERSISTED");
        assertThat(outcome.administrationId()).isEqualTo("DEMO-ADMIN-001");
        assertThat(outcome.persistedAnswerCount()).isZero();
        verifyNoInteractions(responses, students, observations, consent, identities);
        verify(engine).evaluate(definition, submission);
    }

    @Test
    void candidateFieldworkIsDeniedBeforeAnyRepositoryAccess() {
        assertThatThrownBy(() -> service().submit(request("FIELDWORK", "ST-001")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error)
                        .getStatusCode().value()).isEqualTo(403));
        verifyNoInteractions(responses, definitions, students, observations, consent, identities);
        verifyNoInteractions(engine);
    }

    @Test
    void candidateDemoCannotUseNonSyntheticParticipant() {
        assertThatThrownBy(() -> service().submit(request("DEMO", "ST-001")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error)
                        .getStatusCode().value()).isEqualTo(400));
        verifyNoInteractions(responses, definitions, students, observations, consent, identities);
        verifyNoInteractions(engine);
    }
}
