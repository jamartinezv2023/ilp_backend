package com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;

import java.util.Optional;
import java.util.UUID;

@FunctionalInterface
public interface ResearchSubjectIdentityQueryPort {

    Optional<ResearchSubjectIdentity>
    findActiveByParticipantUuid(
            UUID participantUuid
    );
}
