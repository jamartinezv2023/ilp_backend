package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.adapter;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorStatus;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper.ScientificFeatureItemMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper.ScientificFeatureVectorMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureVectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaScientificFeatureVectorAdapterTest {

    private static final Instant CUTOFF =
            Instant.parse(
                    "2026-07-28T12:00:00Z"
            );

    private ScientificFeatureVectorRepository repository;
    private JpaScientificFeatureVectorAdapter adapter;

    @BeforeEach
    void setUp() {
        repository =
                mock(
                        ScientificFeatureVectorRepository.class
                );

        adapter =
                new JpaScientificFeatureVectorAdapter(
                        repository,
                        new ScientificFeatureVectorMapper(
                                new ScientificFeatureItemMapper()
                        )
                );
    }

    @Test
    void shouldSaveVector() {
        ScientificFeatureVector domain =
                domainVector();

        ScientificFeatureVectorEntity expectedEntity =
                entityVector();

        when(repository.save(
                org.mockito.ArgumentMatchers.any(
                        ScientificFeatureVectorEntity.class
                )
        )).thenReturn(expectedEntity);

        ScientificFeatureVector result =
                adapter.save(domain);

        assertThat(result.id())
                .isEqualTo(domain.id());

        assertThat(result.featureCount())
                .isEqualTo(1);

        verify(repository).save(
                org.mockito.ArgumentMatchers.any(
                        ScientificFeatureVectorEntity.class
                )
        );
    }

    @Test
    void shouldCheckLogicalExistence() {
        ParticipantId participantId =
                new ParticipantId("ST-001");

        FeatureSetVersion version =
                new FeatureSetVersion(
                        "ILP_SCIENTIFIC_BASELINE_V1"
                );

        when(
                repository
                        .existsByParticipantIdAndFeatureSetVersionAndFeatureCutoffAt(
                                "ST-001",
                                "ILP_SCIENTIFIC_BASELINE_V1",
                                CUTOFF
                        )
        ).thenReturn(true);

        assertThat(
                adapter.exists(
                        participantId,
                        version,
                        CUTOFF
                )
        ).isTrue();
    }

    @Test
    void shouldFindExactVector() {
        when(
                repository
                        .findByParticipantIdAndFeatureSetVersionAndFeatureCutoffAt(
                                "ST-001",
                                "ILP_SCIENTIFIC_BASELINE_V1",
                                CUTOFF
                        )
        ).thenReturn(
                Optional.of(entityVector())
        );

        assertThat(
                adapter.findExact(
                        new ParticipantId("ST-001"),
                        new FeatureSetVersion(
                                "ILP_SCIENTIFIC_BASELINE_V1"
                        ),
                        CUTOFF
                )
        ).isPresent();
    }

    @Test
    void shouldFindLatestCompletedVector() {
        when(
                repository
                        .findFirstByParticipantIdAndFeatureSetVersionAndStatusOrderByFeatureCutoffAtDescGeneratedAtDesc(
                                "ST-001",
                                "ILP_SCIENTIFIC_BASELINE_V1",
                                ScientificFeatureVectorStatus.COMPLETED
                        )
        ).thenReturn(
                Optional.of(entityVector())
        );

        assertThat(
                adapter.findLatestCompleted(
                        new ParticipantId("ST-001"),
                        new FeatureSetVersion(
                                "ILP_SCIENTIFIC_BASELINE_V1"
                        )
                )
        ).isPresent();
    }

    @Test
    void shouldRejectNullVector() {
        assertThatThrownBy(() ->
                adapter.save(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "scientific feature vector"
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
                CUTOFF.plusSeconds(1),
                1,
                new ScientificChecksum(
                        "CHECKSUM-001"
                ),
                List.of(
                        new ScientificFeatureItem(
                                "SFI-001",
                                new FeatureCode("KOLB_CE"),
                                FeatureValue.numeric(30.0),
                                "KOLB_V1",
                                "ADMIN-001"
                        )
                )
        );
    }

    private ScientificFeatureVectorEntity entityVector() {
        ScientificFeatureVectorMapper mapper =
                new ScientificFeatureVectorMapper(
                        new ScientificFeatureItemMapper()
                );

        return mapper.toEntity(
                domainVector()
        );
    }
}