package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchConsentEligibilityQueryPort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityQueryPort;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public final class ScientificParticipantIdentityPersistenceAdapter
        implements ScientificParticipantIdentityPort {

    private final ResearchConsentEligibilityQueryPort
            consentEligibilityQueryPort;

    private final ResearchSubjectIdentityQueryPort
            identityQueryPort;

    public ScientificParticipantIdentityPersistenceAdapter(
            ResearchConsentEligibilityQueryPort
                    consentEligibilityQueryPort,
            ResearchSubjectIdentityQueryPort
                    identityQueryPort
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
    }

    @Override
    public boolean hasActiveResearchConsent(
            UUID researchParticipantUuid
    ) {
        return consentEligibilityQueryPort
                .hasActiveConsent(
                        Objects.requireNonNull(
                                researchParticipantUuid,
                                "researchParticipantUuid is required"
                        )
                );
    }

    @Override
    public Optional<String> resolveResearchSubjectId(
            UUID researchParticipantUuid
    ) {
        return identityQueryPort
                .findActiveByParticipantUuid(
                        Objects.requireNonNull(
                                researchParticipantUuid,
                                "researchParticipantUuid is required"
                        )
                )
                .map(identity ->
                        identity
                                .getResearchSubjectId()
                                .value()
                                .toString()
                );
    }
}