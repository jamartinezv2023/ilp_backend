package com.inclusive.adaptiveeducationservice.fieldwork.adapter.out.persistence.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectId;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResearchSubjectIdentityPersistenceAdapterTest {

    @Mock
    private ResearchSubjectIdentityJpaRepository repository;

    private ResearchSubjectIdentityPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter =
                new ResearchSubjectIdentityPersistenceAdapter(
                        repository
                );
    }

    @Test
    void persistsAndFindsActiveIdentityByParticipantUuid() {
        UUID participantUuid =
                UUID.randomUUID();

        ResearchSubjectId researchSubjectId =
                ResearchSubjectId.generate();

        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        participantUuid,
                        researchSubjectId
                );

        ResearchSubjectIdentityEntity persisted =
                new ResearchSubjectIdentityEntity(
                        UUID.randomUUID(),
                        participantUuid,
                        researchSubjectId.value(),
                        identity.getCreatedAt(),
                        null
                );

        when(repository.save(any(
                ResearchSubjectIdentityEntity.class
        ))).thenReturn(persisted);

        when(repository.findByParticipantUuidAndDeactivatedAtIsNull(
                participantUuid
        )).thenReturn(Optional.of(persisted));

        ResearchSubjectIdentity saved =
                adapter.save(identity);

        Optional<ResearchSubjectIdentity> result =
                adapter.findActiveByParticipantUuid(
                        participantUuid
                );

        assertEquals(
                identity.getCreatedAt(),
                saved.getCreatedAt()
        );

        assertTrue(result.isPresent());

        assertEquals(
                participantUuid,
                result.orElseThrow().getParticipantUuid()
        );

        assertEquals(
                researchSubjectId,
                result.orElseThrow().getResearchSubjectId()
        );

        assertEquals(
                identity.getCreatedAt(),
                result.orElseThrow().getCreatedAt()
        );

        assertTrue(
                result.orElseThrow().isActive()
        );
    }

    @Test
    void persistsOriginalCreationTime() {
        UUID participantUuid =
                UUID.randomUUID();

        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        participantUuid,
                        ResearchSubjectId.generate()
                );

        ResearchSubjectIdentityEntity persisted =
                new ResearchSubjectIdentityEntity(
                        UUID.randomUUID(),
                        participantUuid,
                        identity.getResearchSubjectId().value(),
                        identity.getCreatedAt(),
                        null
                );

        when(repository.save(any(
                ResearchSubjectIdentityEntity.class
        ))).thenReturn(persisted);

        adapter.save(identity);

        ArgumentCaptor<ResearchSubjectIdentityEntity> captor =
                ArgumentCaptor.forClass(
                        ResearchSubjectIdentityEntity.class
                );

        verify(repository).save(
                captor.capture()
        );

        assertEquals(
                identity.getCreatedAt(),
                captor.getValue().getCreatedAt()
        );
    }

    @Test
    void rehydratesActiveIdentityWithOriginalCreationTime() {
        UUID participantUuid =
                UUID.randomUUID();

        UUID subjectId =
                UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(
                        2026,
                        8,
                        10,
                        10,
                        0
                );

        ResearchSubjectIdentityEntity entity =
                new ResearchSubjectIdentityEntity(
                        UUID.randomUUID(),
                        participantUuid,
                        subjectId,
                        createdAt,
                        null
                );

        when(repository.findByParticipantUuidAndDeactivatedAtIsNull(
                participantUuid
        )).thenReturn(Optional.of(entity));

        Optional<ResearchSubjectIdentity> result =
                adapter.findActiveByParticipantUuid(
                        participantUuid
                );

        assertTrue(result.isPresent());

        ResearchSubjectIdentity identity =
                result.orElseThrow();

        assertEquals(
                participantUuid,
                identity.getParticipantUuid()
        );

        assertEquals(
                new ResearchSubjectId(subjectId),
                identity.getResearchSubjectId()
        );

        assertEquals(
                createdAt,
                identity.getCreatedAt()
        );

        assertTrue(
                identity.isActive()
        );
    }

    @Test
    void returnsEmptyWhenParticipantHasNoActiveIdentity() {
        UUID participantUuid =
                UUID.randomUUID();

        when(repository.findByParticipantUuidAndDeactivatedAtIsNull(
                participantUuid
        )).thenReturn(Optional.empty());

        Optional<ResearchSubjectIdentity> result =
                adapter.findActiveByParticipantUuid(
                        participantUuid
                );

        assertTrue(result.isEmpty());
    }
}
