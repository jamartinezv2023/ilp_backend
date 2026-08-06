package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationRunEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationStatus;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureItemEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorStatus;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureGenerationRunRepository;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureItemRepository;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureVectorRepository;
import jakarta.persistence.EntityManager;
import com.inclusive.adaptiveeducationservice.testsupport.PostgreSqlIntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.transaction.TestTransaction;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.flyway.enabled=true",
                "spring.sql.init.mode=never",
                "spring.jpa.hibernate.ddl-auto=validate"
        }
)
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class ScientificFeatureStorePostgresJpaTest
        extends PostgreSqlIntegrationTestBase {

    private static final String FEATURE_SET =
            "ILP_SCIENTIFIC_BASELINE_V1";

    private static final String GENERATOR_VERSION =
            "SCIENTIFIC_FEATURE_GENERATOR_V1";

    private static final String PARTICIPANT_PREFIX =
            "ST-PG-IT-";

    @Autowired
    private ScientificFeatureVectorRepository vectorRepository;

    @Autowired
    private ScientificFeatureItemRepository itemRepository;

    @Autowired
    private ScientificFeatureGenerationRunRepository runRepository;

    @Autowired
    private EntityManager entityManager;

    private String suffix;
    private String participantId;

    @BeforeEach
    void setUp() {
        suffix = UUID.randomUUID()
                .toString()
                .replace("-", "");

        participantId =
                PARTICIPANT_PREFIX + suffix;
    }

    @AfterEach
    void cleanUp() {
        runRepository
                .findAll()
                .stream()
                .filter(run ->
                        run.getParticipantId()
                                .startsWith(
                                        PARTICIPANT_PREFIX
                                )
                )
                .forEach(runRepository::delete);

        runRepository.flush();

        vectorRepository
                .findAll()
                .stream()
                .filter(vector ->
                        vector.getParticipantId()
                                .startsWith(
                                        PARTICIPANT_PREFIX
                                )
                )
                .forEach(vectorRepository::delete);

        vectorRepository.flush();
        entityManager.clear();
    }

    @Test
    void shouldPersistVectorItemsAndCompletedRunAgainstPostgres() {
        Instant cutoff =
                Instant.parse(
                        "2026-07-25T20:00:00Z"
                );

        ScientificFeatureVectorEntity vector =
                completedVector(
                        "SFV-PG-" + suffix,
                        cutoff,
                        "CHECKSUM-PG-" + suffix
                );

        vector.addItem(
                ScientificFeatureItemEntity.numeric(
                        "SFI-PG-CE-" + suffix,
                        "KOLB_CE",
                        30.0,
                        "KOLB_V1",
                        "ADMIN-PG-" + suffix
                )
        );

        vector.addItem(
                ScientificFeatureItemEntity.text(
                        "SFI-PG-PROFILE-" + suffix,
                        "KOLB_PROFILE",
                        "DIVERGENT",
                        "KOLB_V1",
                        "ADMIN-PG-" + suffix
                )
        );

        vector.addItem(
                ScientificFeatureItemEntity.bool(
                        "SFI-PG-CHANGED-" + suffix,
                        "PROFILE_CHANGED",
                        false,
                        "KOLB_V1",
                        "ADMIN-PG-" + suffix
                )
        );

        vectorRepository.saveAndFlush(vector);

        ScientificFeatureGenerationRunEntity run =
                new ScientificFeatureGenerationRunEntity(
                        "SFGR-PG-" + suffix,
                        participantId,
                        FEATURE_SET,
                        GENERATOR_VERSION,
                        cutoff,
                        cutoff.plusSeconds(1),
                        1
                );

        run.complete(
                vector,
                cutoff.plusSeconds(2)
        );

        runRepository.saveAndFlush(run);
        entityManager.clear();

        ScientificFeatureVectorEntity reloaded =
                vectorRepository
                        .findByParticipantIdAndFeatureSetVersionAndFeatureCutoffAt(
                                participantId,
                                FEATURE_SET,
                                cutoff
                        )
                        .orElseThrow();

        assertThat(reloaded.getItems())
                .hasSize(3);

        assertThat(reloaded.getStatus())
                .isEqualTo(
                        ScientificFeatureVectorStatus.COMPLETED
                );

        assertThat(
                itemRepository
                        .findByFeatureVector_IdOrderByFeatureCodeAsc(
                                vector.getId()
                        )
        )
                .extracting(
                        ScientificFeatureItemEntity::getFeatureCode
                )
                .containsExactly(
                        "KOLB_CE",
                        "KOLB_PROFILE",
                        "PROFILE_CHANGED"
                );

        ScientificFeatureGenerationRunEntity reloadedRun =
                runRepository
                        .findById(run.getId())
                        .orElseThrow();

        assertThat(reloadedRun.getStatus())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.COMPLETED
                );

        assertThat(
                reloadedRun.getFeatureVector().getId()
        ).isEqualTo(vector.getId());
    }

    @Test
    void shouldEnforceLogicalIdempotencyAgainstPostgres() {
        Instant cutoff =
                Instant.parse(
                        "2026-07-25T20:00:00Z"
                );

        vectorRepository.saveAndFlush(
                completedVector(
                        "SFV-PG-FIRST-" + suffix,
                        cutoff,
                        "CHECKSUM-FIRST-" + suffix
                )
        );

        ScientificFeatureVectorEntity duplicate =
                completedVector(
                        "SFV-PG-DUPLICATE-" + suffix,
                        cutoff,
                        "CHECKSUM-DUPLICATE-" + suffix
                );

        assertThatThrownBy(() ->
                vectorRepository.saveAndFlush(
                        duplicate
                )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                );

        /*
         * PostgreSQL marks the transaction as aborted after a
         * constraint violation. Close it explicitly so that
         * @AfterEach can execute cleanup in a valid transaction.
         */
        TestTransaction.flagForRollback();
        TestTransaction.end();
        TestTransaction.start();

        assertThat(
                vectorRepository
                        .existsByParticipantIdAndFeatureSetVersionAndFeatureCutoffAt(
                                participantId,
                                FEATURE_SET,
                                cutoff
                        )
        ).isFalse();
    }

    @Test
    void shouldReturnLatestCompletedVectorAgainstPostgres() {
        Instant older =
                Instant.parse(
                        "2026-07-20T10:00:00Z"
                );

        Instant newer =
                Instant.parse(
                        "2026-07-25T20:00:00Z"
                );

        vectorRepository.saveAndFlush(
                completedVector(
                        "SFV-PG-OLDER-" + suffix,
                        older,
                        "CHECKSUM-OLDER-" + suffix
                )
        );

        vectorRepository.saveAndFlush(
                completedVector(
                        "SFV-PG-NEWER-" + suffix,
                        newer,
                        "CHECKSUM-NEWER-" + suffix
                )
        );

        entityManager.clear();

        ScientificFeatureVectorEntity latest =
                vectorRepository
                        .findFirstByParticipantIdAndFeatureSetVersionAndStatusOrderByFeatureCutoffAtDescGeneratedAtDesc(
                                participantId,
                                FEATURE_SET,
                                ScientificFeatureVectorStatus.COMPLETED
                        )
                        .orElseThrow();

        assertThat(latest.getId())
                .isEqualTo(
                        "SFV-PG-NEWER-" + suffix
                );
    }

    @Test
    void shouldPersistFailedGenerationRunWithoutVectorAgainstPostgres() {
        Instant startedAt =
                Instant.parse(
                        "2026-07-25T20:00:00Z"
                );

        ScientificFeatureGenerationRunEntity run =
                new ScientificFeatureGenerationRunEntity(
                        "SFGR-PG-FAILED-" + suffix,
                        participantId,
                        FEATURE_SET,
                        GENERATOR_VERSION,
                        startedAt,
                        startedAt,
                        0
                );

        run.fail(
                "No observations available",
                startedAt.plusSeconds(1)
        );

        runRepository.saveAndFlush(run);
        entityManager.clear();

        ScientificFeatureGenerationRunEntity reloaded =
                runRepository
                        .findById(run.getId())
                        .orElseThrow();

        assertThat(reloaded.getStatus())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.FAILED
                );

        assertThat(reloaded.getFeatureVector())
                .isNull();

        assertThat(reloaded.getErrorMessage())
                .contains(
                        "No observations"
                );
    }

    private ScientificFeatureVectorEntity completedVector(
            String id,
            Instant cutoff,
            String checksum
    ) {
        return new ScientificFeatureVectorEntity(
                id,
                participantId,
                FEATURE_SET,
                GENERATOR_VERSION,
                cutoff,
                cutoff.plusSeconds(1),
                1,
                ScientificFeatureVectorStatus.COMPLETED,
                checksum
        );
    }
}