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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.sql.init.mode=never",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
class ScientificFeatureStoreJpaTest {

    private static final String FEATURE_SET =
            "ILP_SCIENTIFIC_BASELINE_V1";

    private static final String GENERATOR_VERSION =
            "SCIENTIFIC_FEATURE_GENERATOR_V1";

    @Autowired
    private ScientificFeatureVectorRepository vectorRepository;

    @Autowired
    private ScientificFeatureItemRepository itemRepository;

    @Autowired
    private ScientificFeatureGenerationRunRepository runRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistVectorItemsAndCompletedRun() {
        Instant cutoff =
                Instant.parse("2026-07-24T20:00:00Z");

        ScientificFeatureVectorEntity vector =
                completedVector(
                        "SFV-001",
                        cutoff,
                        "CHECKSUM-001"
                );

        vector.addItem(
                ScientificFeatureItemEntity.numeric(
                        "SFI-CE-001",
                        "KOLB_CE",
                        30.0,
                        "KOLB_V1",
                        "ADMIN-001"
                )
        );

        vector.addItem(
                ScientificFeatureItemEntity.text(
                        "SFI-PROFILE-001",
                        "KOLB_PROFILE",
                        "DIVERGENT",
                        "KOLB_V1",
                        "ADMIN-001"
                )
        );

        vector.addItem(
                ScientificFeatureItemEntity.bool(
                        "SFI-CHANGED-001",
                        "PROFILE_CHANGED",
                        false,
                        "KOLB_V1",
                        "ADMIN-001"
                )
        );

        vectorRepository.saveAndFlush(vector);

        ScientificFeatureGenerationRunEntity run =
                new ScientificFeatureGenerationRunEntity(
                        "SFGR-001",
                        "ST-001",
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
                                "ST-001",
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
                                "SFV-001"
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
                        .findById("SFGR-001")
                        .orElseThrow();

        assertThat(reloadedRun.getStatus())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.COMPLETED
                );

        assertThat(
                reloadedRun.getFeatureVector().getId()
        ).isEqualTo("SFV-001");
    }

    @Test
    void shouldEnforceLogicalVectorIdempotency() {
        Instant cutoff =
                Instant.parse("2026-07-24T20:00:00Z");

        vectorRepository.saveAndFlush(
                completedVector(
                        "SFV-FIRST",
                        cutoff,
                        "CHECKSUM-FIRST"
                )
        );

        ScientificFeatureVectorEntity duplicate =
                completedVector(
                        "SFV-DUPLICATE",
                        cutoff,
                        "CHECKSUM-DUPLICATE"
                );

        assertThatThrownBy(() ->
                vectorRepository.saveAndFlush(duplicate)
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                );
    }

    @Test
    void shouldReturnLatestCompletedVector() {
        Instant older =
                Instant.parse("2026-07-20T10:00:00Z");

        Instant newer =
                Instant.parse("2026-07-24T20:00:00Z");

        vectorRepository.saveAndFlush(
                completedVector(
                        "SFV-OLDER",
                        older,
                        "CHECKSUM-OLDER"
                )
        );

        vectorRepository.saveAndFlush(
                completedVector(
                        "SFV-NEWER",
                        newer,
                        "CHECKSUM-NEWER"
                )
        );

        entityManager.clear();

        ScientificFeatureVectorEntity latest =
                vectorRepository
                        .findFirstByParticipantIdAndFeatureSetVersionAndStatusOrderByFeatureCutoffAtDescGeneratedAtDesc(
                                "ST-001",
                                FEATURE_SET,
                                ScientificFeatureVectorStatus.COMPLETED
                        )
                        .orElseThrow();

        assertThat(latest.getId())
                .isEqualTo("SFV-NEWER");
    }

    @Test
    void shouldCascadeItemsWhenVectorIsDeleted() {
        Instant cutoff =
                Instant.parse("2026-07-24T20:00:00Z");

        ScientificFeatureVectorEntity vector =
                completedVector(
                        "SFV-CASCADE",
                        cutoff,
                        "CHECKSUM-CASCADE"
                );

        vector.addItem(
                ScientificFeatureItemEntity.numeric(
                        "SFI-CASCADE",
                        "KOLB_CE",
                        30.0,
                        "KOLB_V1",
                        "ADMIN-001"
                )
        );

        vectorRepository.saveAndFlush(vector);

        assertThat(itemRepository.count())
                .isEqualTo(1);

        vectorRepository.delete(vector);
        vectorRepository.flush();

        assertThat(itemRepository.count())
                .isZero();
    }

    @Test
    void shouldRejectDuplicateFeatureCodeInAggregate() {
        ScientificFeatureVectorEntity vector =
                completedVector(
                        "SFV-DUPLICATE-CODE",
                        Instant.parse(
                                "2026-07-24T20:00:00Z"
                        ),
                        "CHECKSUM"
                );

        vector.addItem(
                ScientificFeatureItemEntity.numeric(
                        "SFI-ONE",
                        "KOLB_CE",
                        30.0,
                        "KOLB_V1",
                        "ADMIN-001"
                )
        );

        assertThatThrownBy(() ->
                vector.addItem(
                        ScientificFeatureItemEntity.numeric(
                                "SFI-TWO",
                                "KOLB_CE",
                                31.0,
                                "KOLB_V1",
                                "ADMIN-001"
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "Duplicate feature code"
                );
    }

    @Test
    void shouldPersistFailedGenerationRunWithoutVector() {
        Instant startedAt =
                Instant.parse("2026-07-24T20:00:00Z");

        ScientificFeatureGenerationRunEntity run =
                new ScientificFeatureGenerationRunEntity(
                        "SFGR-FAILED",
                        "ST-001",
                        FEATURE_SET,
                        GENERATOR_VERSION,
                        startedAt,
                        startedAt,
                        0
                );

        run.fail(
                "No scientific observations available",
                startedAt.plusSeconds(1)
        );

        runRepository.saveAndFlush(run);

        entityManager.clear();

        ScientificFeatureGenerationRunEntity reloaded =
                runRepository
                        .findById("SFGR-FAILED")
                        .orElseThrow();

        assertThat(reloaded.getStatus())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.FAILED
                );

        assertThat(reloaded.getFeatureVector())
                .isNull();

        assertThat(reloaded.getErrorMessage())
                .contains(
                        "No scientific observations"
                );
    }

    private ScientificFeatureVectorEntity completedVector(
            String id,
            Instant cutoff,
            String checksum
    ) {
        return new ScientificFeatureVectorEntity(
                id,
                "ST-001",
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