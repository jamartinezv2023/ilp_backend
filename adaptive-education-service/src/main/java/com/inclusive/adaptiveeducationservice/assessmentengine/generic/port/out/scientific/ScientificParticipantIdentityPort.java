package com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific;

import java.util.Optional;
import java.util.UUID;

public interface ScientificParticipantIdentityPort {

    boolean hasActiveResearchConsent(
            UUID researchParticipantUuid
    );

    Optional<String> resolveResearchSubjectId(
            UUID researchParticipantUuid
    );
}