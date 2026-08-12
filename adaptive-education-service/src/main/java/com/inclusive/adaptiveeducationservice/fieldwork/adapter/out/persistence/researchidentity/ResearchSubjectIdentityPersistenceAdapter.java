package com.inclusive.adaptiveeducationservice.fieldwork.adapter.out.persistence.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectId;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityPersistencePort;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchSubjectIdentityQueryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ResearchSubjectIdentityPersistenceAdapter
        implements ResearchSubjectIdentityPersistencePort,
        ResearchSubjectIdentityQueryPort {

    private final ResearchSubjectIdentityJpaRepository repository;

    public ResearchSubjectIdentityPersistenceAdapter(
            ResearchSubjectIdentityJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public ResearchSubjectIdentity save(
            ResearchSubjectIdentity identity
    ) {
        ResearchSubjectIdentityEntity entity =
                new ResearchSubjectIdentityEntity(
                        identity.getParticipantUuid(),
                        identity.getResearchSubjectId().value(),
                        identity.getCreatedAt(),
                        identity.getDeactivatedAt()
                );

        ResearchSubjectIdentityEntity persisted =
                repository.save(entity);

        return toDomain(persisted);
    }

    @Override
    public Optional<ResearchSubjectIdentity>
    findActiveByParticipantUuid(
            UUID participantUuid
    ) {
        return repository
                .findByParticipantUuidAndDeactivatedAtIsNull(
                        participantUuid
                )
                .map(this::toDomain);
    }

    private ResearchSubjectIdentity toDomain(
            ResearchSubjectIdentityEntity entity
    ) {
        return ResearchSubjectIdentity.rehydrate(
                entity.getParticipantUuid(),
                new ResearchSubjectId(
                        entity.getResearchSubjectId()
                ),
                entity.getCreatedAt(),
                entity.getDeactivatedAt()
        );
    }
}
