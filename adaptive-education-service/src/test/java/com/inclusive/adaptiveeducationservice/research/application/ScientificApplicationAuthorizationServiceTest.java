package com.inclusive.adaptiveeducationservice.research.application;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission.SubmitAssessmentService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ResearchParticipant;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
class ScientificApplicationAuthorizationServiceTest {
    private final UUID user = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID tenant = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private final UUID grantId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID participantId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private final UUID consentId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private ScientificApplicationGrantPort grants;
    private ResearchParticipantRepository participants;
    private ConsentRecordRepository consents;
    private ScientificParticipantIdentityPort identities;
    private SubmitAssessmentService submissions;
    private ResearchParticipant participant;
    private ConsentRecord consent;
    private ScientificApplicationGrant grant;
    private ScientificApplicationAuthorizationService service;
    @BeforeEach
    void prepareValidAssignment() {
        grants = mock(ScientificApplicationGrantPort.class);
        participants = mock(ResearchParticipantRepository.class);
        consents = mock(ConsentRecordRepository.class);
        identities = mock(ScientificParticipantIdentityPort.class);
        submissions = mock(SubmitAssessmentService.class);
        participant = mock(ResearchParticipant.class);
        consent = mock(ConsentRecord.class);
        grant = new ScientificApplicationGrant(
                grantId, user, tenant, "SYNTHETIC-STUDENT-001",
                participantId, "SYNTHETIC-SUBJECT-001", consentId,
                "test-consent-v1", "SYNTHETIC_RESEARCH",
                "KOLB_V1", "0.0.1-test", true
        );
        when(grants.findById(grantId)).thenReturn(Optional.of(grant));
        when(participants.findById(participantId)).thenReturn(Optional.of(participant));
        when(participant.getParticipantCode()).thenReturn("SYNTHETIC-PARTICIPANT-001");
        when(consents.findById(consentId)).thenReturn(Optional.of(consent));
        when(consents.findFirstByParticipantCodeOrderByCreatedAtDesc(
                "SYNTHETIC-PARTICIPANT-001"
        )).thenReturn(Optional.of(consent));
        when(consent.getParticipantCode()).thenReturn("SYNTHETIC-PARTICIPANT-001");
        when(consent.getConsentId()).thenReturn(consentId);
        when(consent.getConsentType()).thenReturn("SYNTHETIC_RESEARCH");
        when(consent.getStatus()).thenReturn("APPROVED");
        when(consent.getApprovedAt()).thenReturn(LocalDateTime.of(2026, 10, 3, 9, 0));
        when(identities.hasActiveResearchConsent(participantId)).thenReturn(true);
        when(identities.resolveResearchSubjectId(participantId))
                .thenReturn(Optional.of("SYNTHETIC-SUBJECT-001"));
        service = new ScientificApplicationAuthorizationService(
                grants, participants, consents, identities, submissions
        );
    }
    @Test
    void shouldAuthorizeValidAssignmentWithoutSubmitting() {
        assertThat(service.authorize(user, tenant, tenant, grantId)).isEqualTo(grant);
        verifyNoInteractions(submissions);
    }
    @Test
    void shouldRejectMissingActorAndAssignmentIdentifiersWithoutSubmitting() {
        assertThatThrownBy(() -> service.authorize(null, tenant, tenant, grantId))
                .isInstanceOfSatisfying(ResponseStatusException.class, error ->
                        assertThat(error.getStatusCode().value()).isEqualTo(401));
        assertThatThrownBy(() -> service.authorize(user, null, tenant, grantId))
                .isInstanceOfSatisfying(ResponseStatusException.class, error ->
                        assertThat(error.getStatusCode().value()).isEqualTo(401));
        assertThatThrownBy(() -> service.authorize(user, tenant, null, grantId))
                .isInstanceOfSatisfying(ResponseStatusException.class, error ->
                        assertThat(error.getStatusCode().value()).isEqualTo(403));
        assertThatThrownBy(() -> service.authorize(user, tenant, tenant, null))
                .isInstanceOfSatisfying(ResponseStatusException.class, error ->
                        assertThat(error.getStatusCode().value()).isEqualTo(403));
        verifyNoInteractions(submissions);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "unknown-grant", "inactive-grant", "wrong-user",
            "wrong-header-tenant", "wrong-assignment-tenant", "missing-participant",
            "missing-consent", "missing-latest-consent", "wrong-participant-code",
            "wrong-consent-type", "superseded-consent", "pending-consent",
            "missing-approval-time", "withdrawn-consent", "inactive-consent",
            "missing-subject", "wrong-subject"
    })
    void shouldRejectUnauthorizedActorOrAssignmentWithoutSubmitting(String scenario) {
        UUID actor = user;
        UUID tokenTenant = tenant;
        UUID headerTenant = tenant;
        UUID requestedGrant = grantId;
        switch (scenario) {
            case "unknown-grant" -> when(grants.findById(grantId)).thenReturn(Optional.empty());
            case "inactive-grant" -> when(grants.findById(grantId)).thenReturn(Optional.of(
                    new ScientificApplicationGrant(
                            grantId, user, tenant, grant.studentId(), participantId,
                            grant.researchSubjectId(), consentId, grant.consentVersion(),
                            grant.consentType(), grant.assessmentCode(), grant.assessmentVersion(), false
                    )
            ));
            case "wrong-user" -> actor = UUID.randomUUID();
            case "wrong-header-tenant" -> headerTenant = UUID.randomUUID();
            case "wrong-assignment-tenant" -> {
                tokenTenant = UUID.randomUUID();
                headerTenant = tokenTenant;
            }
            case "missing-participant" ->
                    when(participants.findById(participantId)).thenReturn(Optional.empty());
            case "missing-consent" ->
                    when(consents.findById(consentId)).thenReturn(Optional.empty());
            case "missing-latest-consent" -> when(
                    consents.findFirstByParticipantCodeOrderByCreatedAtDesc("SYNTHETIC-PARTICIPANT-001")
            ).thenReturn(Optional.empty());
            case "wrong-participant-code" -> when(consent.getParticipantCode()).thenReturn("OTHER");
            case "wrong-consent-type" -> when(consent.getConsentType()).thenReturn("OTHER");
            case "superseded-consent" -> {
                var latest = mock(ConsentRecord.class);
                when(latest.getConsentId()).thenReturn(UUID.randomUUID());
                when(consents.findFirstByParticipantCodeOrderByCreatedAtDesc(
                        "SYNTHETIC-PARTICIPANT-001"
                )).thenReturn(Optional.of(latest));
            }
            case "pending-consent" -> when(consent.getStatus()).thenReturn("PENDING");
            case "missing-approval-time" -> when(consent.getApprovedAt()).thenReturn(null);
            case "withdrawn-consent" ->
                    when(consent.getWithdrawnAt()).thenReturn(LocalDateTime.of(2026, 10, 3, 10, 0));
            case "inactive-consent" ->
                    when(identities.hasActiveResearchConsent(participantId)).thenReturn(false);
            case "missing-subject" ->
                    when(identities.resolveResearchSubjectId(participantId)).thenReturn(Optional.empty());
            case "wrong-subject" ->
                    when(identities.resolveResearchSubjectId(participantId)).thenReturn(Optional.of("OTHER"));
            default -> throw new IllegalArgumentException(scenario);
        }
        var finalActor = actor;
        var finalTokenTenant = tokenTenant;
        var finalHeaderTenant = headerTenant;
        var finalGrant = requestedGrant;
        int expectedStatus = 403;
        assertThatThrownBy(() -> service.authorize(
                finalActor, finalTokenTenant, finalHeaderTenant, finalGrant
        )).isInstanceOfSatisfying(ResponseStatusException.class, error ->
                assertThat(error.getStatusCode().value()).isEqualTo(expectedStatus));
        verifyNoInteractions(submissions);
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "student", "research-participant", "instrument", "instrument-version",
            "consent", "consent-version", "missing-context"
    })
    void shouldRejectTamperedSubmissionWithoutInvokingSubmissionService(String scenario) {
        var context = validContext();
        String student = grant.studentId();
        UUID researchParticipant = participantId;
        String instrument = grant.assessmentCode();
        String version = grant.assessmentVersion();
        switch (scenario) {
            case "student" -> student = "OTHER";
            case "research-participant" -> researchParticipant = UUID.randomUUID();
            case "instrument" -> instrument = "OTHER";
            case "instrument-version" -> version = "OTHER";
            case "consent" -> context.put("consentId", UUID.randomUUID().toString());
            case "consent-version" -> context.put("consentVersion", "OTHER");
            case "missing-context" -> context.clear();
            default -> throw new IllegalArgumentException(scenario);
        }
        var request = new SubmitAssessmentRequest(
                "SYNTHETIC-ADMIN-001", student, researchParticipant,
                instrument, version, List.of(), context, Instant.parse("2026-10-03T10:00:00Z")
        );
        assertThatThrownBy(() -> service.submit(user, tenant, tenant, grantId, request))
                .isInstanceOfSatisfying(ResponseStatusException.class, error ->
                        assertThat(error.getStatusCode().value()).isEqualTo(403));
        verifyNoInteractions(submissions);
    }
    @Test
    void shouldReplaceClientProvenanceAndPreserveSubmissionData() {
        var context = validContext();
        context.put("authorizedUserId", "FORGED");
        context.put("authorizedTenantId", "FORGED");
        context.put("authorizationGrantId", "FORGED");
        context.put("authorizationSource", "CLIENT");
        context.put("language", "es");
        var request = new SubmitAssessmentRequest(
                "SYNTHETIC-ADMIN-001", grant.studentId(), participantId,
                grant.assessmentCode(), grant.assessmentVersion(), List.of(),
                context, Instant.parse("2026-10-03T10:00:00Z")
        );
        var response = mock(SubmitAssessmentResponse.class);
        when(submissions.submit(any(SubmitAssessmentRequest.class))).thenReturn(response);
        assertThat(service.submit(user, tenant, tenant, grantId, request)).isSameAs(response);
        var captured = ArgumentCaptor.forClass(SubmitAssessmentRequest.class);
        verify(submissions).submit(captured.capture());
        var persisted = captured.getValue();
        assertThat(persisted.administrationId()).isEqualTo(request.administrationId());
        assertThat(persisted.participantId()).isEqualTo(grant.studentId());
        assertThat(persisted.researchParticipantUuid()).isEqualTo(participantId);
        assertThat(persisted.assessmentCode()).isEqualTo(grant.assessmentCode());
        assertThat(persisted.assessmentVersion()).isEqualTo(grant.assessmentVersion());
        assertThat(persisted.responses()).isEqualTo(request.responses());
        assertThat(persisted.submittedAt()).isEqualTo(request.submittedAt());
        assertThat(persisted.context())
                .containsEntry("authorizedUserId", user.toString())
                .containsEntry("authorizedTenantId", tenant.toString())
                .containsEntry("authorizationGrantId", grantId.toString())
                .containsEntry("authorizationSource", "SERVER_ASSIGNMENT")
                .containsEntry("language", "es")
                .containsEntry("consentId", consentId.toString())
                .containsEntry("consentVersion", grant.consentVersion());
        assertThat(request.context()).containsEntry("authorizationSource", "CLIENT");
    }
    private Map<String, String> validContext() {
        var context = new LinkedHashMap<String, String>();
        context.put("consentId", consentId.toString());
        context.put("consentVersion", grant.consentVersion());
        return context;
    }
}
