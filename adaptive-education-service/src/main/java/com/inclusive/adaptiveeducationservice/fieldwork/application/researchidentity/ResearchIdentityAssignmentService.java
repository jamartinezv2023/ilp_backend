package com.inclusive.adaptiveeducationservice.fieldwork.application.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectId;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchConsentEligibilityQueryPort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityPersistencePort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityQueryPort;

import java.util.Objects;
import java.util.UUID;

public final class ResearchIdentityAssignmentService {

    private final ResearchConsentEligibilityQueryPort
            consentEligibilityQueryPort;

    private final ResearchSubjectIdentityQueryPort
            identityQueryPort;

    private final ResearchSubjectIdentityPersistencePort
            identityPersistencePort;

    public ResearchIdentityAssignmentService(
            ResearchConsentEligibilityQueryPort
                    consentEligibilityQueryPort,
            ResearchSubjectIdentityQueryPort
                    identityQueryPort,
            ResearchSubjectIdentityPersistencePort
                    identityPersistencePort
    ) {
        this.consentEligibilityQueryPort =
                Objects.requireNonNull(
                        consentEligibilityQueryPort,
                        "consentEligibilityQueryPort is required"
                );

        this.identityQueryPort =
                Objects.requireNonNull(
                        identityQueryPort,
                        "identityQueryPort is required"
                );

        this.identityPersistencePort =
                Objects.requireNonNull(
                        identityPersistencePort,
                        "identityPersistencePort is required"
                );
    }

    public ResearchSubjectIdentity assign(
            UUID participantUuid
    ) {
        UUID requiredParticipantUuid =
                Objects.requireNonNull(
                        participantUuid,
                        "participantUuid is required"
                );

        var existingIdentity =
                Objects.requireNonNull(
                        identityQueryPort
                                .findActiveByParticipantUuid(
                                        requiredParticipantUuid
                                ),
                        "identityQueryPort result is required"
                );

        if (existingIdentity.isPresent()) {
            return existingIdentity.get();
        }

        if (
                !consentEligibilityQueryPort
                        .hasActiveConsent(
                                requiredParticipantUuid
                        )
        ) {
            throw new ResearchConsentRequiredException(
                    requiredParticipantUuid
            );
        }

        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        requiredParticipantUuid,
                        ResearchSubjectId.generate()
                );

        return Objects.requireNonNull(
                identityPersistencePort.save(identity),
                "persisted research subject identity is required"
        );
    }
}
