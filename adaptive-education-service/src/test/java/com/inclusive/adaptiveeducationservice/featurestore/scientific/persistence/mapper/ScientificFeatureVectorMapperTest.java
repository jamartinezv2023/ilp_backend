package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureItemEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureVectorMapperTest {

    private static final Instant CUTOFF =
            Instant.parse(
                    "2026-07-27T20:00:00Z"
            );

    private static final Instant GENERATED_AT =
            CUTOFF.plusSeconds(2);

    private final ScientificFeatureItemMapper itemMapper =
            new ScientificFeatureItemMapper();

    private final ScientificFeatureVectorMapper mapper =
            new ScientificFeatureVectorMapper(
                    itemMapper
            );

    @Test
    void shouldMapCompletedDomainVectorToEntity() {
        ScientificFeatureVector domain =
                domainVector();

        ScientificFeatureVectorEntity entity =
                mapper.toEntity(domain);

        assertThat(entity.getId())
                .isEqualTo("SFV-001");

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

        assertThat(entity.getGeneratedAt())
                .isEqualTo(GENERATED_AT);

        assertThat(entity.getSourceObservationCount())
                .isEqualTo(2);

        assertThat(entity.getStatus())
                .isEqualTo(
                        ScientificFeatureVectorStatus.COMPLETED
                );

        assertThat(entity.getChecksum())
                .isEqualTo("CHECKSUM-001");

        assertThat(entity.getItems())
                .hasSize(3);

        assertThat(entity.getItems())
                .allSatisfy(item ->
                        assertThat(item.getFeatureVector())
                                .isSameAs(entity)
                );
    }

    @Test
    void shouldMapCompletedEntityToDomain() {
        ScientificFeatureVectorEntity entity =
                completedEntity();

        ScientificFeatureVector domain =
                mapper.toDomain(entity);

        assertThat(domain.id())
                .isEqualTo(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        )
                );

        assertThat(domain.participantId())
                .isEqualTo(
                        new ParticipantId("ST-001")
                );

        assertThat(domain.featureSetVersion())
                .isEqualTo(
                        new FeatureSetVersion(
                                "ILP_SCIENTIFIC_BASELINE_V1"
                        )
                );

        assertThat(domain.generatorVersion())
                .isEqualTo(
                        new GeneratorVersion(
                                "SCIENTIFIC_FEATURE_GENERATOR_V1"
                        )
                );

        assertThat(domain.featureCutoffAt())
                .isEqualTo(CUTOFF);

        assertThat(domain.generatedAt())
                .isEqualTo(GENERATED_AT);

        assertThat(domain.sourceObservationCount())
                .isEqualTo(2);

        assertThat(domain.checksum())
                .isEqualTo(
                        new ScientificChecksum(
                                "CHECKSUM-001"
                        )
                );

        assertThat(domain.featureCount())
                .isEqualTo(3);

        assertThat(
                domain.requireFeature(
                        new FeatureCode("KOLB_CE")
                ).value()
        )
                .isEqualTo(
                        FeatureValue.numeric(30.5)
                );

        assertThat(
                domain.requireFeature(
                        new FeatureCode("KOLB_PROFILE")
                ).value()
        )
                .isEqualTo(
                        FeatureValue.text("DIVERGENT")
                );

        assertThat(
                domain.requireFeature(
                        new FeatureCode("PROFILE_CHANGED")
                ).value()
        )
                .isEqualTo(
                        FeatureValue.bool(false)
                );
    }

    @Test
    void shouldRoundTripCompletedVector() {
        ScientificFeatureVector original =
                domainVector();

        ScientificFeatureVector restored =
                mapper.toDomain(
                        mapper.toEntity(original)
                );

        assertThat(restored.id())
                .isEqualTo(original.id());

        assertThat(restored.participantId())
                .isEqualTo(original.participantId());

        assertThat(restored.featureSetVersion())
                .isEqualTo(
                        original.featureSetVersion()
                );

        assertThat(restored.generatorVersion())
                .isEqualTo(
                        original.generatorVersion()
                );

        assertThat(restored.featureCutoffAt())
                .isEqualTo(
                        original.featureCutoffAt()
                );

        assertThat(restored.generatedAt())
                .isEqualTo(
                        original.generatedAt()
                );

        assertThat(restored.sourceObservationCount())
                .isEqualTo(
                        original.sourceObservationCount()
                );

        assertThat(restored.checksum())
                .isEqualTo(original.checksum());

        assertThat(restored.items())
                .containsExactlyInAnyOrderElementsOf(
                        original.items()
                );
    }

    @Test
    void shouldRejectGeneratingEntity() {
        ScientificFeatureVectorEntity entity =
                entityWithStatus(
                        ScientificFeatureVectorStatus.GENERATING
                );

        assertThatThrownBy(() ->
                mapper.toDomain(entity)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "Only COMPLETED"
                )
                .hasMessageContaining(
                        "GENERATING"
                );
    }

    @Test
    void shouldRejectFailedEntity() {
        ScientificFeatureVectorEntity entity =
                entityWithStatus(
                        ScientificFeatureVectorStatus.FAILED
                );

        assertThatThrownBy(() ->
                mapper.toDomain(entity)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "Only COMPLETED"
                )
                .hasMessageContaining(
                        "FAILED"
                );
    }

    @Test
    void shouldRejectCompletedEntityWithoutItems() {
        ScientificFeatureVectorEntity entity =
                new ScientificFeatureVectorEntity(
                        "SFV-EMPTY",
                        "ST-001",
                        "ILP_SCIENTIFIC_BASELINE_V1",
                        "SCIENTIFIC_FEATURE_GENERATOR_V1",
                        CUTOFF,
                        GENERATED_AT,
                        0,
                        ScientificFeatureVectorStatus.COMPLETED,
                        "CHECKSUM-EMPTY"
                );

        assertThatThrownBy(() ->
                mapper.toDomain(entity)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "items must not be empty"
                );
    }

    @Test
    void shouldRejectNullDomainVector() {
        assertThatThrownBy(() ->
                mapper.toEntity(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "domain scientific feature vector"
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
                        "scientific feature vector entity"
                );
    }

    @Test
    void shouldRejectNullItemMapper() {
        assertThatThrownBy(() ->
                new ScientificFeatureVectorMapper(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "itemMapper"
                );
    }

    private ScientificFeatureVector domainVector() {
        return new ScientificFeatureVector(
                new ScientificFeatureVectorId(
                        "SFV-001"
                ),
                new ParticipantId("ST-001"),
                new FeatureSetVersion(
                        "ILP_SCIENTIFIC_BASELINE_V1"
                ),
                new GeneratorVersion(
                        "SCIENTIFIC_FEATURE_GENERATOR_V1"
                ),
                CUTOFF,
                GENERATED_AT,
                2,
                new ScientificChecksum(
                        "CHECKSUM-001"
                ),
                List.of(
                        domainNumericItem(),
                        domainTextItem(),
                        domainBooleanItem()
                )
        );
    }

    private ScientificFeatureVectorEntity completedEntity() {
        return entityWithStatus(
                ScientificFeatureVectorStatus.COMPLETED
        );
    }

    private ScientificFeatureVectorEntity entityWithStatus(
            ScientificFeatureVectorStatus status
    ) {
        ScientificFeatureVectorEntity entity =
                new ScientificFeatureVectorEntity(
                        "SFV-001",
                        "ST-001",
                        "ILP_SCIENTIFIC_BASELINE_V1",
                        "SCIENTIFIC_FEATURE_GENERATOR_V1",
                        CUTOFF,
                        GENERATED_AT,
                        2,
                        status,
                        "CHECKSUM-001"
                );

        entity.addItem(
                ScientificFeatureItemEntity.numeric(
                        "SFI-NUMERIC",
                        "KOLB_CE",
                        30.5,
                        "KOLB_V1",
                        "ADMIN-001"
                )
        );

        entity.addItem(
                ScientificFeatureItemEntity.text(
                        "SFI-TEXT",
                        "KOLB_PROFILE",
                        "DIVERGENT",
                        "KOLB_V1",
                        "ADMIN-001"
                )
        );

        entity.addItem(
                ScientificFeatureItemEntity.bool(
                        "SFI-BOOLEAN",
                        "PROFILE_CHANGED",
                        false,
                        "KOLB_V1",
                        "ADMIN-001"
                )
        );

        return entity;
    }

    private ScientificFeatureItem domainNumericItem() {
        return new ScientificFeatureItem(
                "SFI-NUMERIC",
                new FeatureCode("KOLB_CE"),
                FeatureValue.numeric(30.5),
                "KOLB_V1",
                "ADMIN-001"
        );
    }

    private ScientificFeatureItem domainTextItem() {
        return new ScientificFeatureItem(
                "SFI-TEXT",
                new FeatureCode("KOLB_PROFILE"),
                FeatureValue.text("DIVERGENT"),
                "KOLB_V1",
                "ADMIN-001"
        );
    }

    private ScientificFeatureItem domainBooleanItem() {
        return new ScientificFeatureItem(
                "SFI-BOOLEAN",
                new FeatureCode("PROFILE_CHANGED"),
                FeatureValue.bool(false),
                "KOLB_V1",
                "ADMIN-001"
        );
    }
}