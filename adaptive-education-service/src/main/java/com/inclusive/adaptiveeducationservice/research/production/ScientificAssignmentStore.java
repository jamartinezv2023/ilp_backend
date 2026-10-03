package com.inclusive.adaptiveeducationservice.research.production;

import com.inclusive.adaptiveeducationservice.research.application.ScientificApplicationGrant;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

/** Rows are provisioned by a reviewed institutional process, never by request headers. */
public final class ScientificAssignmentStore {
    private final JdbcTemplate jdbc;

    public ScientificAssignmentStore(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public ScientificAssignment lock(ScientificActor actor, UUID assignmentId) {
        List<ScientificAssignment> rows = jdbc.query("""
                SELECT a.*, b.student_id, b.research_subject_id, c.evidence_id,
                       c.legacy_consent_id, c.accepted_by, d.document_id,
                       d.document_version, d.document_sha256, d.document_text, c.consent_type
                FROM scientific_assignments a
                JOIN scientific_memberships m ON m.tenant_id = a.tenant_id AND m.user_id = a.user_id
                JOIN scientific_participant_bindings b ON b.tenant_id = a.tenant_id
                     AND b.participant_uuid = a.participant_uuid
                JOIN scientific_consent_evidence c ON c.tenant_id = a.tenant_id
                     AND c.evidence_id = a.consent_evidence_id AND c.participant_uuid = a.participant_uuid
                JOIN scientific_consent_documents d ON d.tenant_id = c.tenant_id
                     AND d.document_id = c.document_id
                JOIN scientific_instrument_permissions i ON i.tenant_id = a.tenant_id
                     AND i.assessment_code = a.assessment_code AND i.assessment_version = a.assessment_version
                WHERE a.assignment_id = ? AND a.user_id = ? AND a.tenant_id = ?
                  AND a.active = TRUE AND m.active = TRUE AND b.active = TRUE
                  AND c.withdrawn_at IS NULL AND d.active = TRUE AND i.approved = TRUE
                  AND c.accepted_document_sha256 = d.document_sha256
                  AND a.valid_until > CURRENT_TIMESTAMP
                FOR UPDATE
                """, (rs, row) -> {
                    if (!ScientificSnapshot.sha256(rs.getString("document_text"))
                            .equals(rs.getString("document_sha256"))) { throw denied(); }
                    return new ScientificAssignment(new ScientificApplicationGrant(
                        rs.getObject("assignment_id", UUID.class), rs.getObject("user_id", UUID.class),
                        rs.getObject("tenant_id", UUID.class), rs.getString("student_id"),
                        rs.getObject("participant_uuid", UUID.class), rs.getString("research_subject_id"),
                        rs.getObject("legacy_consent_id", UUID.class), rs.getString("document_version"),
                        rs.getString("consent_type"), rs.getString("assessment_code"),
                        rs.getString("assessment_version"), true), rs.getObject("evidence_id", UUID.class),
                        rs.getObject("document_id", UUID.class), rs.getString("document_sha256"),
                        rs.getObject("accepted_by", UUID.class), rs.getBoolean("can_submit"),
                        rs.getBoolean("can_read"), rs.getBoolean("can_export"));
                },
                assignmentId, actor.userId(), actor.tenantId());
        if (rows.size() != 1) { throw denied(); }
        return rows.get(0);
    }

    public UUID withdrawEvidence(UUID evidenceId, ScientificActor actor) {
        var rows = jdbc.query("""
                SELECT legacy_consent_id FROM scientific_consent_evidence
                WHERE evidence_id = ? AND tenant_id = ? AND accepted_by = ? FOR UPDATE
                """, (rs, row) -> rs.getObject("legacy_consent_id", UUID.class),
                evidenceId, actor.tenantId(), actor.userId());
        if (rows.size() != 1) { throw denied(); }
        jdbc.update("""
                UPDATE scientific_consent_evidence SET withdrawn_at = CURRENT_TIMESTAMP, withdrawn_by = ?
                WHERE evidence_id = ? AND tenant_id = ? AND withdrawn_at IS NULL
                """, actor.userId(), evidenceId, actor.tenantId());
        return rows.get(0);
    }

    public static ResponseStatusException denied() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "SCIENTIFIC_ACCESS_DENIED");
    }
}
