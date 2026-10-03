package com.inclusive.adaptiveeducationservice.research.production;

import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentRequest;
import com.inclusive.adaptiveeducationservice.api.assessmentsubmission.SubmitAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission.SubmitAssessmentService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import com.inclusive.adaptiveeducationservice.research.application.ScientificApplicationAuthorizationService;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** JDBC authorization rows and JPA submission share the same transaction/data source. */
public class ScientificProductionService {
    private final ScientificAssignmentStore assignments;
    private final ResearchParticipantRepository participants;
    private final ConsentRecordRepository consents;
    private final ScientificParticipantIdentityPort identities;
    private final SubmitAssessmentService submissions;
    private final GetParticipantAssessmentScientificHistoryService histories;
    private final AssessmentResponseService responses;

    public ScientificProductionService(ScientificAssignmentStore assignments,
            ResearchParticipantRepository participants, ConsentRecordRepository consents,
            ScientificParticipantIdentityPort identities, SubmitAssessmentService submissions,
            GetParticipantAssessmentScientificHistoryService histories, AssessmentResponseService responses) {
        this.assignments = assignments;
        this.participants = participants;
        this.consents = consents;
        this.identities = identities;
        this.submissions = submissions;
        this.histories = histories;
        this.responses = responses;
    }

    private ScientificApplicationAuthorizationService core(ScientificAssignment assignment) {
        return new ScientificApplicationAuthorizationService(id ->
                assignment.grant().id().equals(id) ? Optional.of(assignment.grant()) : Optional.empty(),
                participants, consents, identities, submissions);
    }

    private ScientificAssignment authorize(ScientificActor actor, UUID tenant, UUID id) {
        if (!actor.tenantId().equals(tenant)) { throw ScientificAssignmentStore.denied(); }
        var assignment = assignments.lock(actor, id);
        core(assignment).authorize(actor.userId(), actor.tenantId(), tenant, id);
        return assignment;
    }

    @Transactional
    public SubmitAssessmentResponse submit(ScientificActor actor, UUID tenant, UUID id,
            SubmitAssessmentRequest request) {
        var assignment = authorize(actor, tenant, id);
        if (!assignment.canSubmit()) { throw ScientificAssignmentStore.denied(); }
        var context = new LinkedHashMap<>(request.context());
        context.put("institutionId", actor.tenantId().toString());
        context.put("consentDocumentId", assignment.documentId().toString());
        context.put("consentDocumentSha256", assignment.documentSha256());
        context.put("consentEvidenceId", assignment.evidenceId().toString());
        return core(assignment).submit(actor.userId(), actor.tenantId(), tenant, id,
                new SubmitAssessmentRequest(request.administrationId(), request.participantId(),
                        request.researchParticipantUuid(), request.assessmentCode(), request.assessmentVersion(),
                        request.responses(), context, request.submittedAt()));
    }

    private List<AssessmentScientificObservation> scopedHistory(ScientificAssignment assignment) {
        var grant = assignment.grant();
        return histories.getByParticipantId(grant.researchSubjectId()).observations().stream()
                .filter(row -> grant.assessmentCode().equals(row.assessmentCode())
                        && grant.assessmentVersion().equals(row.assessmentVersion())
                        && row.context() != null
                        && Objects.equals(grant.id().toString(), row.context().completeContext().get("authorizationGrantId"))
                        && Objects.equals(grant.tenantId().toString(), row.context().completeContext().get("authorizedTenantId"))
                        && Objects.equals(assignment.evidenceId().toString(), row.context().completeContext().get("consentEvidenceId"))
                        && Objects.equals(assignment.documentSha256(), row.context().completeContext().get("consentDocumentSha256"))
                        && Objects.equals(grant.consentVersion(), row.context().consentVersion()))
                .toList();
    }

    @Transactional
    public List<AssessmentScientificObservation> history(ScientificActor actor, UUID tenant, UUID id) {
        var assignment = authorize(actor, tenant, id);
        if (!assignment.canRead()) { throw ScientificAssignmentStore.denied(); }
        return scopedHistory(assignment);
    }

    @Transactional
    public ScientificSnapshot snapshot(ScientificActor actor, UUID tenant, UUID id, String administrationId) {
        var assignment = authorize(actor, tenant, id);
        if (!assignment.canExport()) { throw ScientificAssignmentStore.denied(); }
        var observation = scopedHistory(assignment).stream()
                .filter(row -> administrationId.equals(row.administrationId())).findFirst()
                .orElseThrow(ScientificAssignmentStore::denied);
        var response = responses.findById(administrationId);
        if (!assignment.grant().studentId().equals(response.studentId())
                || !observation.assessmentCode().equals(response.assessmentCode())
                || !observation.assessmentVersion().equals(response.assessmentVersion())) {
            throw ScientificAssignmentStore.denied();
        }
        return ScientificSnapshot.create(assignment, response, observation);
    }

    @Transactional
    public void withdraw(ScientificActor actor, UUID tenant, UUID evidenceId) {
        if (!actor.tenantId().equals(tenant)) { throw ScientificAssignmentStore.denied(); }
        var consentId = assignments.withdrawEvidence(evidenceId, actor);
        var consent = consents.findById(consentId).orElseThrow(ScientificAssignmentStore::denied);
        if (consent.getWithdrawnAt() == null) {
            consent.withdraw(LocalDateTime.now());
            consents.saveAndFlush(consent);
        }
    }
}
