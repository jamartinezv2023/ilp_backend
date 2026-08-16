package com.inclusive.adaptiveeducationservice.fieldwork.adapter.out.persistence.researchidentity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResearchSubjectIdentityJpaRepository
        extends JpaRepository<
        ResearchSubjectIdentityEntity,
        UUID
        > {

    Optional<ResearchSubjectIdentityEntity>
    findByParticipantUuidAndDeactivatedAtIsNull(
            UUID participantUuid
    );
}
