package com.inclusive.adaptiveeducationservice.research.application;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission.SubmitAssessmentService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;

/** Authorization core; not registered as a bean and not exposed as a production endpoint.
 * Actor identifiers must come from a cryptographically verified access token.
 */
public final class ScientificApplicationAuthorizationService {
    private final ScientificApplicationGrantPort grants;
    private final ResearchParticipantRepository participants;
    private final ConsentRecordRepository consents;
    private final ScientificParticipantIdentityPort identities;
    private final SubmitAssessmentService submissions;

    public ScientificApplicationAuthorizationService(
            ScientificApplicationGrantPort grants, ResearchParticipantRepository participants,
            ConsentRecordRepository consents, ScientificParticipantIdentityPort identities,
            SubmitAssessmentService submissions
    ) {
        this.grants = Objects.requireNonNull(grants);
        this.participants = Objects.requireNonNull(participants);
        this.consents = Objects.requireNonNull(consents);
        this.identities = Objects.requireNonNull(identities);
        this.submissions = Objects.requireNonNull(submissions);
    }

    public ScientificApplicationGrant authorize(UUID userId, UUID tokenTenantId,
            UUID headerTenantId, UUID grantId) {
        if (userId == null || tokenTenantId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "SCIENTIFIC_ACTOR_REQUIRED");
        }
        if (grantId == null || headerTenantId == null) { throw denied(); }
        var grant = grants.findById(grantId).orElseThrow(ScientificApplicationAuthorizationService::denied);
        if (!grant.active() || !userId.equals(grant.userId())
                || !tokenTenantId.equals(headerTenantId) || !tokenTenantId.equals(grant.tenantId())) {
            throw denied();
        }
        var participant = participants.findById(grant.researchParticipantUuid())
                .orElseThrow(ScientificApplicationAuthorizationService::denied);
        var consent = consents.findById(grant.consentId())
                .orElseThrow(ScientificApplicationAuthorizationService::denied);
        var latest = consents.findFirstByParticipantCodeOrderByCreatedAtDesc(participant.getParticipantCode())
                .orElseThrow(ScientificApplicationAuthorizationService::denied);
        if (!participant.getParticipantCode().equals(consent.getParticipantCode())
                || !grant.consentType().equals(consent.getConsentType())
                || !latest.getConsentId().equals(consent.getConsentId())
                || !"APPROVED".equalsIgnoreCase(consent.getStatus())
                || consent.getApprovedAt() == null || consent.getWithdrawnAt() != null
                || !identities.hasActiveResearchConsent(grant.researchParticipantUuid())
                || !identities.resolveResearchSubjectId(grant.researchParticipantUuid())
                        .filter(grant.researchSubjectId()::equals).isPresent()) {
            throw denied();
        }
        return grant;
    }

    public SubmitAssessmentResponse submit(UUID userId, UUID tokenTenantId,
            UUID headerTenantId, UUID grantId, SubmitAssessmentRequest request) {
        var grant = authorize(userId, tokenTenantId, headerTenantId, grantId);
        if (!grant.studentId().equals(request.participantId())
                || !grant.researchParticipantUuid().equals(request.researchParticipantUuid())
                || !grant.assessmentCode().equals(request.assessmentCode())
                || !grant.assessmentVersion().equals(request.assessmentVersion())
                || !grant.consentId().toString().equals(request.context().get("consentId"))
                || !grant.consentVersion().equals(request.context().get("consentVersion"))) {
            throw denied();
        }
        var context = new LinkedHashMap<>(request.context());
        // These keys always come from the verified actor and trusted assignment.
        context.put("authorizedUserId", userId.toString());
        context.put("authorizedTenantId", grant.tenantId().toString());
        context.put("authorizationGrantId", grant.id().toString());
        context.put("authorizationSource", "SERVER_ASSIGNMENT");
        return submissions.submit(new SubmitAssessmentRequest(request.administrationId(),
                grant.studentId(), grant.researchParticipantUuid(), grant.assessmentCode(),
                grant.assessmentVersion(), request.responses(), context, request.submittedAt()));
    }

    private static ResponseStatusException denied() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "SCIENTIFIC_APPLICATION_FORBIDDEN");
    }
}
