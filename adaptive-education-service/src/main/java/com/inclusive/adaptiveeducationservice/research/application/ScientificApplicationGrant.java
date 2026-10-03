package com.inclusive.adaptiveeducationservice.research.application;

import java.util.UUID;

/** Server-owned assignment. An adapter must load this record from trusted storage. */
public record ScientificApplicationGrant(
        UUID id, UUID userId, UUID tenantId, String studentId,
        UUID researchParticipantUuid, String researchSubjectId, UUID consentId,
        String consentVersion, String consentType, String assessmentCode, String assessmentVersion,
        boolean active
) {
}
