package com.inclusive.adaptiveeducationservice.fieldwork.adapter.out.persistence.researchidentity;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectId;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity.ResearchSubjectIdentity;
import com.inclusive.adaptiveeducationservice.testsupport.PostgreSqlIntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@SpringBootTest
class ResearchSubjectIdentityPostgreSqlIntegrationTest
        extends PostgreSqlIntegrationTestBase {

    @Autowired
    private ResearchSubjectIdentityPersistenceAdapter adapter;

    @Autowired
    private ResearchSubjectIdentityJpaRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldPersistAndReloadActiveResearchIdentityFromPostgreSql() {
        UUID participantUuid =
                UUID.randomUUID();

        ResearchSubjectId researchSubjectId =
                ResearchSubjectId.generate();

        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        participantUuid,
                        researchSubjectId
                );

        LocalDateTime originalCreatedAt =
                identity.getCreatedAt();

        ResearchSubjectIdentity saved =
                adapter.save(identity);

        Optional<ResearchSubjectIdentity> reloaded =
                adapter.findActiveByParticipantUuid(
                        participantUuid
                );

        assertThat(saved.getParticipantUuid())
                .isEqualTo(participantUuid);

        assertThat(saved.getResearchSubjectId())
                .isEqualTo(researchSubjectId);

        assertThat(saved.getCreatedAt())
                .isEqualTo(originalCreatedAt);

        assertThat(saved.isActive())
                .isTrue();

        assertThat(reloaded)
                .isPresent();

        ResearchSubjectIdentity persisted =
                reloaded.orElseThrow();

        assertThat(persisted.getParticipantUuid())
                .isEqualTo(participantUuid);

        assertThat(persisted.getResearchSubjectId())
                .isEqualTo(researchSubjectId);

        assertThat(persisted.getCreatedAt())
                .isEqualTo(originalCreatedAt);

        assertThat(persisted.getDeactivatedAt())
                .isNull();

        assertThat(persisted.isActive())
                .isTrue();

        Integer rowCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM fieldwork_research_subject_identities
                        WHERE participant_uuid = ?
                          AND research_subject_id = ?
                          AND deactivated_at IS NULL
                        """,
                        Integer.class,
                        participantUuid,
                        researchSubjectId.value()
                );

        assertThat(rowCount)
                .isEqualTo(1);
    }

    @Test
    void shouldNotReturnDeactivatedResearchIdentityAsActive() {
        UUID participantUuid =
                UUID.randomUUID();

        ResearchSubjectId researchSubjectId =
                ResearchSubjectId.generate();

        LocalDateTime createdAt =
                LocalDateTime.of(
                        2026,
                        8,
                        10,
                        10,
                        0
                );

        LocalDateTime deactivatedAt =
                createdAt.plusHours(2);

        jdbcTemplate.update(
                """
                INSERT INTO fieldwork_research_subject_identities (
                    id,
                    participant_uuid,
                    research_subject_id,
                    created_at,
                    deactivated_at
                )
                VALUES (?, ?, ?, ?, ?)
                """,
                UUID.randomUUID(),
                participantUuid,
                researchSubjectId.value(),
                createdAt,
                deactivatedAt
        );

        Optional<ResearchSubjectIdentity> result =
                adapter.findActiveByParticipantUuid(
                        participantUuid
                );

        assertThat(result)
                .isEmpty();

        Integer persistedCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM fieldwork_research_subject_identities
                        WHERE participant_uuid = ?
                          AND deactivated_at IS NOT NULL
                        """,
                        Integer.class,
                        participantUuid
                );

        assertThat(persistedCount)
                .isEqualTo(1);
    }
}
