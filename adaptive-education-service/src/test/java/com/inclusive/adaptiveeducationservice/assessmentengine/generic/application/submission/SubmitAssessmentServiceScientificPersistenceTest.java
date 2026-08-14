package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.PersistAssessmentScientificObservationCommand;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.AssessmentDefinitionPersistenceMapper;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificObservationPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificObservationConsentEligibilityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubmitAssessmentServiceScientificPersistenceTest {

    private AssessmentResponseRepository responseRepository;
    private AssessmentDefinitionRepository definitionRepository;
    private AssessmentDefinitionPersistenceMapper definitionMapper;
    private StudentProfileRepository studentProfileRepository;
    private GenericAssessmentEngine assessmentEngine;
    private SubmitAssessmentMapper submissionMapper;
    private AssessmentScientificObservationPort
            scientificObservationPort;

    private ScientificObservationConsentEligibilityPort
            consentEligibilityPort;

    private ScientificParticipantIdentityPort
            scientificParticipantIdentityPort;

    private SubmitAssessmentService service;

    private SubmitAssessmentRequest request;
    private AssessmentSubmission submission;
    private AssessmentResult result;

    @BeforeEach
    void setUp() {
        responseRepository =
                mock(AssessmentResponseRepository.class);

        definitionRepository =
                mock(AssessmentDefinitionRepository.class);

        definitionMapper =
                mock(
                        AssessmentDefinitionPersistenceMapper.class
                );

        studentProfileRepository =
                mock(StudentProfileRepository.class);

        assessmentEngine =
                mock(GenericAssessmentEngine.class);

        submissionMapper =
                mock(SubmitAssessmentMapper.class);

        scientificObservationPort =
                mock(
                        AssessmentScientificObservationPort.class
                );

        consentEligibilityPort =

                mock(

                        ScientificObservationConsentEligibilityPort.class

                );

        when(
                consentEligibilityPort
                        .hasActiveConsentForParticipantCode(
                                "ST-001"
                        )
        ).thenReturn(true);
        scientificParticipantIdentityPort =
                mock(
                        ScientificParticipantIdentityPort.class
                );


        service =
                new SubmitAssessmentService(
                        responseRepository,
                        definitionRepository,
                        definitionMapper,
                        studentProfileRepository,
                        assessmentEngine,
                        submissionMapper,
                        scientificObservationPort,
                        consentEligibilityPort,
                        scientificParticipantIdentityPort
                );

        Instant submittedAt =
                Instant.parse(
                        "2026-07-23T10:00:00Z"
                );

        request =
                new SubmitAssessmentRequest(
                        "ADMIN-001",
                        "ST-001",
                        UUID.fromString(
                                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
                        ),
                        "KOLB_V1",
                        "1.0",
                        List.of(),
                        Map.of(
                                "source",
                                "WEB"
                        ),
                        submittedAt
                );

        submission =
                new AssessmentSubmission(
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        List.of(),
                        Map.of(
                                "source",
                                "WEB"
                        ),
                        submittedAt
                );

        result =
                new AssessmentResult(
                        "ADMIN-001",
                        "ST-001",
                        "KOLB_V1",
                        "1.0",
                        "DIVERGENT",
                        Map.of(
                                "CE",
                                48.0
                        ),
                        Map.of(
                                "PRIMARY_PROFILE",
                                "Perfil divergente"
                        ),
                        List.of(),
                        "KOLB_BASELINE_V1",
                        submittedAt.plusSeconds(1)
                );
    }

    @Test
    void shouldPersistRawAndScientificObservation() {
        prepareSuccessfulSubmission();

        service.submit(request);

        ArgumentCaptor<
                PersistAssessmentScientificObservationCommand
                > commandCaptor =
                ArgumentCaptor.forClass(
                        PersistAssessmentScientificObservationCommand.class
                );

        verify(scientificObservationPort)
                .save(commandCaptor.capture());

        assertThat(
                commandCaptor.getValue()
                        .submission()
                        .administrationId()
        ).isEqualTo("ADMIN-001");

        assertThat(
                commandCaptor.getValue()
                        .result()
                        .administrationId()
        ).isEqualTo("ADMIN-001");

        InOrder persistenceOrder =
                inOrder(
                        responseRepository,
                        scientificObservationPort
                );

        persistenceOrder
                .verify(responseRepository)
                .save(
                        any(
                                AssessmentResponseEntity.class
                        )
                );

        persistenceOrder
                .verify(scientificObservationPort)
                .save(any());
    }

    @Test
    void shouldRejectExistingScientificObservation() {
        when(
                responseRepository.existsById(
                        "ADMIN-001"
                )
        ).thenReturn(false);

        when(
                scientificObservationPort
                        .existsByAdministrationId(
                                "ADMIN-001"
                        )
        ).thenReturn(true);

        assertThatThrownBy(() ->
                service.submit(request)
        )
                .isInstanceOf(
                        ResponseStatusException.class
                )
                .satisfies(exception ->
                        assertThat(
                                (
                                        (ResponseStatusException)
                                                exception
                                )
                                        .getStatusCode()
                                        .value()
                        ).isEqualTo(409)
                );

        verify(assessmentEngine, never())
                .evaluate(any(), any());

        verify(responseRepository, never())
                .save(any());

        verify(scientificObservationPort, never())
                .save(any());
    }

    @Test
    void shouldPropagateScientificPersistenceFailure() {
        prepareSuccessfulSubmission();

        doThrow(
                new IllegalStateException(
                        "Simulated scientific persistence failure"
                )
        )
                .when(scientificObservationPort)
                .save(any());

        assertThatThrownBy(() ->
                service.submit(request)
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "Simulated scientific persistence failure"
                );

        verify(responseRepository)
                .save(
                        any(
                                AssessmentResponseEntity.class
                        )
                );

        verify(scientificObservationPort)
                .save(any());
    }

    private void prepareSuccessfulSubmission() {

        when(
                scientificParticipantIdentityPort
                        .hasActiveResearchConsent(
                                UUID.fromString(
                                        "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
                                )
                        )
        ).thenReturn(true);

        when(
                scientificParticipantIdentityPort
                        .resolveResearchSubjectId(
                                UUID.fromString(
                                        "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
                                )
                        )
        ).thenReturn(
                Optional.of(
                        "11111111-1111-1111-1111-111111111111"
                )
        );
        AssessmentDefinitionEntity definitionEntity =
                mock(
                        AssessmentDefinitionEntity.class
                );

        AssessmentDefinition definition =
                mock(
                        AssessmentDefinition.class
                );

        when(
                responseRepository.existsById(
                        "ADMIN-001"
                )
        ).thenReturn(false);

        when(
                scientificObservationPort
                        .existsByAdministrationId(
                                "ADMIN-001"
                        )
        ).thenReturn(false);

        when(
                studentProfileRepository.existsById(
                        "ST-001"
                )
        ).thenReturn(true);

        when(
                definitionRepository
                        .findByCodeAndActiveTrue(
                                "KOLB_V1"
                        )
        ).thenReturn(
                Optional.of(definitionEntity)
        );

        when(definitionEntity.getVersion())
                .thenReturn("1.0");

        when(
                definitionMapper.toDomain(
                        definitionEntity
                )
        ).thenReturn(definition);

        when(definition.questions())
                .thenReturn(List.of());

        when(submissionMapper.toDomain(request))
                .thenReturn(submission);

        when(
                assessmentEngine.evaluate(
                        definition,
                        submission
                )
        ).thenReturn(result);

        when(
                responseRepository.save(
                        any(
                                AssessmentResponseEntity.class
                        )
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );
    }

    @Test
    void shouldRejectSubmissionWhenResearchConsentIsNotActive() {

        when(
                responseRepository.existsById(
                        "ADMIN-001"
                )
        ).thenReturn(false);

        when(
                scientificObservationPort
                        .existsByAdministrationId(
                                "ADMIN-001"
                        )
        ).thenReturn(false);

        when(
                studentProfileRepository.existsById(
                        "ST-001"
                )
        ).thenReturn(true);

        when(
                consentEligibilityPort
                        .hasActiveConsentForParticipantCode(
                                "ST-001"
                        )
        ).thenReturn(false);

        assertThatThrownBy(
                () -> service.submit(request)
        )
                .isInstanceOf(
                        ResponseStatusException.class
                )
                .satisfies(exception ->
                        assertThat(
                                (
                                        (ResponseStatusException)
                                                exception
                                )
                                        .getStatusCode()
                                        .value()
                        ).isEqualTo(403)
                );

        verify(
                assessmentEngine,
                never()
        ).evaluate(
                any(),
                any()
        );

        verify(
                responseRepository,
                never()
        ).save(
                any()
        );

        verify(
                scientificObservationPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldResolvePseudonymousResearchSubjectBeforeScientificPersistence() {

        UUID researchParticipantUuid =
                UUID.fromString(
                        "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
                );

        request =
                new SubmitAssessmentRequest(
                        "ADMIN-001",
                        "ST-001",
                        researchParticipantUuid,
                        "KOLB_V1",
                        "1.0",
                        List.of(),
                        Map.of(
                                "source",
                                "WEB"
                        ),
                        Instant.parse(
                                "2026-07-23T10:00:00Z"
                        )
                );

        prepareSuccessfulSubmission();

        when(
                scientificParticipantIdentityPort
                        .resolveResearchSubjectId(
                                researchParticipantUuid
                        )
        ).thenReturn(
                Optional.of(
                        "11111111-1111-1111-1111-111111111111"
                )
        );

        service.submit(request);

        verify(
                studentProfileRepository
        ).existsById(
                "ST-001"
        );

        verify(
                scientificParticipantIdentityPort
        ).resolveResearchSubjectId(
                researchParticipantUuid
        );

        ArgumentCaptor<
                PersistAssessmentScientificObservationCommand
                > commandCaptor =
                ArgumentCaptor.forClass(
                        PersistAssessmentScientificObservationCommand.class
                );

        verify(
                scientificObservationPort
        ).save(
                commandCaptor.capture()
        );

        PersistAssessmentScientificObservationCommand command =
                commandCaptor.getValue();

        assertThat(
                command.researchSubjectId()
        ).isEqualTo(
                "11111111-1111-1111-1111-111111111111"
        );

        assertThat(
                command
                        .submission()
                        .participantId()
        ).isEqualTo(
                "ST-001"
        );

        assertThat(
                command
                        .result()
                        .participantId()
        ).isEqualTo(
                "ST-001"
        );
    }
}