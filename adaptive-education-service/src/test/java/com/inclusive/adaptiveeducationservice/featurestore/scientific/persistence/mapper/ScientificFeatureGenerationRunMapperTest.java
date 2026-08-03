package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationRun;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationStatus;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationRunEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureGenerationRunMapperTest {

    private static final Instant CUTOFF =
            Instant.parse(
                    "2026-07-28T05:00:00Z"
            );

    private static final Instant STARTED_AT =
            CUTOFF.plusSeconds(1);

    private static final Instant COMPLETED_AT =
            STARTED_AT.plusSeconds(4);

    private final ScientificFeatureGenerationRunMapper mapper =
            new ScientificFeatureGenerationRunMapper();

    @Test
    void shouldMapStartedDomainRunToEntity() {
        ScientificFeatureGenerationRun domain =
                startedDomainRun();

        ScientificFeatureGenerationRunEntity entity =
                mapper.toEntity(
                        domain,
                        null
                );

        assertThat(entity.getId())
                .isEqualTo("SFGR-001");

        assertThat(entity.getParticipantId())
                .isEqualTo("ST-001");

        assertThat(entity.getFeatureSetVersion())
                .isEqualTo(
                        "ILP_SCIENTIFIC_BASELINE_V1"
                );

        assertThat(entity.getGeneratorVersion())
                .isEqualTo(
                        "SCIENTIFIC_FEATURE_GENERATOR_V1"
                );

        assertThat(entity.getFeatureCutoffAt())
                .isEqualTo(CUTOFF);

        assertThat(entity.getStartedAt())
                .isEqualTo(STARTED_AT);

        assertThat(entity.getInputObservationCount())
                .isEqualTo(2);

        assertThat(entity.getStatus())
                .isEqualTo(
                        com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationStatus.STARTED
                );

        assertThat(entity.getCompletedAt())
                .isNull();

        assertThat(entity.getErrorMessage())
                .isNull();

        assertThat(entity.getFeatureVector())
                .isNull();
    }

    @Test
    void shouldMapCompletedDomainRunToEntity() {
        ScientificFeatureGenerationRun domain =
                startedDomainRun()
                        .complete(
                                new ScientificFeatureVectorId(
                                        "SFV-001"
                                ),
                                COMPLETED_AT
                        );

        ScientificFeatureVectorEntity vectorEntity =
                vectorEntity();

        ScientificFeatureGenerationRunEntity entity =
                mapper.toEntity(
                        domain,
                        vectorEntity
                );

        assertThat(entity.getStatus())
                .isEqualTo(
                        com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationStatus.COMPLETED
                );

        assertThat(entity.getCompletedAt())
                .isEqualTo(COMPLETED_AT);

        assertThat(entity.getFeatureVector())
                .isSameAs(vectorEntity);

        assertThat(entity.getErrorMessage())
                .isNull();
    }

    @Test
    void shouldMapFailedDomainRunToEntity() {
        ScientificFeatureGenerationRun domain =
                startedDomainRun()
                        .fail(
                                "Generation failed",
                                COMPLETED_AT
                        );

        ScientificFeatureGenerationRunEntity entity =
                mapper.toEntity(
                        domain,
                        null
                );

        assertThat(entity.getStatus())
                .isEqualTo(
                        com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationStatus.FAILED
                );

        assertThat(entity.getCompletedAt())
                .isEqualTo(COMPLETED_AT);

        assertThat(entity.getErrorMessage())
                .isEqualTo("Generation failed");

        assertThat(entity.getFeatureVector())
                .isNull();
    }

    @Test
    void shouldMapStartedEntityToDomain() {
        ScientificFeatureGenerationRunEntity entity =
                startedEntity();

        ScientificFeatureGenerationRun domain =
                mapper.toDomain(entity);

        assertThat(domain.id())
                .isEqualTo(
                        new ScientificFeatureGenerationRunId(
                                "SFGR-001"
                        )
                );

        assertThat(domain.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.STARTED
                );

        assertThat(domain.completedAt())
                .isNull();

        assertThat(domain.featureVectorId())
                .isNull();

        assertThat(domain.errorMessage())
                .isNull();
    }

    @Test
    void shouldMapCompletedEntityToDomain() {
        ScientificFeatureGenerationRunEntity entity =
                startedEntity();

        entity.complete(
                vectorEntity(),
                COMPLETED_AT
        );

        ScientificFeatureGenerationRun domain =
                mapper.toDomain(entity);

        assertThat(domain.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.COMPLETED
                );

        assertThat(domain.completedAt())
                .isEqualTo(COMPLETED_AT);

        assertThat(domain.featureVectorId())
                .isEqualTo(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        )
                );

        assertThat(domain.errorMessage())
                .isNull();
    }

    @Test
    void shouldMapFailedEntityToDomain() {
        ScientificFeatureGenerationRunEntity entity =
                startedEntity();

        entity.fail(
                "Generation failed",
                COMPLETED_AT
        );

        ScientificFeatureGenerationRun domain =
                mapper.toDomain(entity);

        assertThat(domain.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.FAILED
                );

        assertThat(domain.completedAt())
                .isEqualTo(COMPLETED_AT);

        assertThat(domain.errorMessage())
                .isEqualTo("Generation failed");

        assertThat(domain.featureVectorId())
                .isNull();
    }

    @Test
    void shouldRoundTripStartedRun() {
        ScientificFeatureGenerationRun original =
                startedDomainRun();

        ScientificFeatureGenerationRun restored =
                mapper.toDomain(
                        mapper.toEntity(
                                original,
                                null
                        )
                );

        assertCommonFields(
                restored,
                original
        );

        assertThat(restored.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.STARTED
                );
    }

    @Test
    void shouldRoundTripCompletedRun() {
        ScientificFeatureGenerationRun original =
                startedDomainRun()
                        .complete(
                                new ScientificFeatureVectorId(
                                        "SFV-001"
                                ),
                                COMPLETED_AT
                        );

        ScientificFeatureGenerationRun restored =
                mapper.toDomain(
                        mapper.toEntity(
                                original,
                                vectorEntity()
                        )
                );

        assertCommonFields(
                restored,
                original
        );

        assertThat(restored.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.COMPLETED
                );

        assertThat(restored.featureVectorId())
                .isEqualTo(
                        original.featureVectorId()
                );

        assertThat(restored.completedAt())
                .isEqualTo(
                        original.completedAt()
                );
    }

    @Test
    void shouldRoundTripFailedRun() {
        ScientificFeatureGenerationRun original =
                startedDomainRun()
                        .fail(
                                "Generation failed",
                                COMPLETED_AT
                        );

        ScientificFeatureGenerationRun restored =
                mapper.toDomain(
                        mapper.toEntity(
                                original,
                                null
                        )
                );

        assertCommonFields(
                restored,
                original
        );

        assertThat(restored.status())
                .isEqualTo(
                        ScientificFeatureGenerationStatus.FAILED
                );

        assertThat(restored.errorMessage())
                .isEqualTo(
                        original.errorMessage()
                );

        assertThat(restored.completedAt())
                .isEqualTo(
                        original.completedAt()
                );
    }

    @Test
    void shouldRejectCompletedRunWithoutVectorEntity() {
        ScientificFeatureGenerationRun completed =
                startedDomainRun()
                        .complete(
                                new ScientificFeatureVectorId(
                                        "SFV-001"
                                ),
                                COMPLETED_AT
                        );

        assertThatThrownBy(() ->
                mapper.toEntity(
                        completed,
                        null
                )
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "vectorEntity"
                );
    }

    @Test
    void shouldRejectMismatchedVectorEntity() {
        ScientificFeatureGenerationRun completed =
                startedDomainRun()
                        .complete(
                                new ScientificFeatureVectorId(
                                        "SFV-001"
                                ),
                                COMPLETED_AT
                        );

        ScientificFeatureVectorEntity otherVector =
                new ScientificFeatureVectorEntity(
                        "SFV-OTHER",
                        "ST-001",
                        "ILP_SCIENTIFIC_BASELINE_V1",
                        "SCIENTIFIC_FEATURE_GENERATOR_V1",
                        CUTOFF,
                        COMPLETED_AT,
                        2,
                        ScientificFeatureVectorStatus.COMPLETED,
                        "CHECKSUM-OTHER"
                );

        assertThatThrownBy(() ->
                mapper.toEntity(
                        completed,
                        otherVector
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "does not match"
                );
    }

    @Test
    void shouldRejectVectorForStartedRun() {
        assertThatThrownBy(() ->
                mapper.toEntity(
                        startedDomainRun(),
                        vectorEntity()
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "STARTED"
                );
    }

    @Test
    void shouldRejectVectorForFailedRun() {
        ScientificFeatureGenerationRun failed =
                startedDomainRun()
                        .fail(
                                "Generation failed",
                                COMPLETED_AT
                        );

        assertThatThrownBy(() ->
                mapper.toEntity(
                        failed,
                        vectorEntity()
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "FAILED"
                );
    }

    @Test
    void shouldRejectNullDomainRun() {
        assertThatThrownBy(() ->
                mapper.toEntity(
                        null,
                        null
                )
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "domain scientific feature generation run"
                );
    }

    @Test
    void shouldRejectNullEntity() {
        assertThatThrownBy(() ->
                mapper.toDomain(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "scientific feature generation run entity"
                );
    }

    private ScientificFeatureGenerationRun startedDomainRun() {
        return ScientificFeatureGenerationRun.start(
                new ScientificFeatureGenerationRunId(
                        "SFGR-001"
                ),
                new ParticipantId("ST-001"),
                new FeatureSetVersion(
                        "ILP_SCIENTIFIC_BASELINE_V1"
                ),
                new GeneratorVersion(
                        "SCIENTIFIC_FEATURE_GENERATOR_V1"
                ),
                CUTOFF,
                STARTED_AT,
                2
        );
    }

    private ScientificFeatureGenerationRunEntity startedEntity() {
        return new ScientificFeatureGenerationRunEntity(
                "SFGR-001",
                "ST-001",
                "ILP_SCIENTIFIC_BASELINE_V1",
                "SCIENTIFIC_FEATURE_GENERATOR_V1",
                CUTOFF,
                STARTED_AT,
                2
        );
    }

    private ScientificFeatureVectorEntity vectorEntity() {
        return new ScientificFeatureVectorEntity(
                "SFV-001",
                "ST-001",
                "ILP_SCIENTIFIC_BASELINE_V1",
                "SCIENTIFIC_FEATURE_GENERATOR_V1",
                CUTOFF,
                COMPLETED_AT,
                2,
                ScientificFeatureVectorStatus.COMPLETED,
                "CHECKSUM-001"
        );
    }

    private void assertCommonFields(
            ScientificFeatureGenerationRun actual,
            ScientificFeatureGenerationRun expected
    ) {
        assertThat(actual.id())
                .isEqualTo(expected.id());

        assertThat(actual.participantId())
                .isEqualTo(
                        expected.participantId()
                );

        assertThat(actual.featureSetVersion())
                .isEqualTo(
                        expected.featureSetVersion()
                );

        assertThat(actual.generatorVersion())
                .isEqualTo(
                        expected.generatorVersion()
                );

        assertThat(actual.featureCutoffAt())
                .isEqualTo(
                        expected.featureCutoffAt()
                );

        assertThat(actual.startedAt())
                .isEqualTo(expected.startedAt());

        assertThat(actual.inputObservationCount())
                .isEqualTo(
                        expected.inputObservationCount()
                );
    }
}