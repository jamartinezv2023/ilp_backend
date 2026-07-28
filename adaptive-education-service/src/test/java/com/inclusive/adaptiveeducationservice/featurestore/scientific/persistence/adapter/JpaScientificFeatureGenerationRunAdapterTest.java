package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.adapter;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationRun;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureGenerationRunEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorEntity;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureVectorStatus;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper.ScientificFeatureGenerationRunMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureGenerationRunRepository;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.repository.ScientificFeatureVectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaScientificFeatureGenerationRunAdapterTest {

    private static final Instant CUTOFF =
            Instant.parse(
                    "2026-07-28T12:00:00Z"
            );

    private ScientificFeatureGenerationRunRepository
            runRepository;

    private ScientificFeatureVectorRepository
            vectorRepository;

    private JpaScientificFeatureGenerationRunAdapter
            adapter;

    @BeforeEach
    void setUp() {
        runRepository =
                mock(
                        ScientificFeatureGenerationRunRepository.class
                );

        vectorRepository =
                mock(
                        ScientificFeatureVectorRepository.class
                );

        adapter =
                new JpaScientificFeatureGenerationRunAdapter(
                        runRepository,
                        vectorRepository,
                        new ScientificFeatureGenerationRunMapper()
                );
    }

    @Test
    void shouldSaveStartedRun() {
        ScientificFeatureGenerationRun domain =
                startedRun();

        ScientificFeatureGenerationRunEntity entity =
                new ScientificFeatureGenerationRunMapper()
                        .toEntity(
                                domain,
                                null
                        );

        when(runRepository.save(any()))
                .thenReturn(entity);

        ScientificFeatureGenerationRun result =
                adapter.save(domain);

        assertThat(result.isStarted())
                .isTrue();

        verify(runRepository).save(any());
    }

    @Test
    void shouldSaveCompletedRunUsingPersistedVector() {
        ScientificFeatureGenerationRun domain =
                startedRun()
                        .complete(
                                new ScientificFeatureVectorId(
                                        "SFV-001"
                                ),
                                CUTOFF.plusSeconds(5)
                        );

        ScientificFeatureVectorEntity vector =
                vectorEntity();

        ScientificFeatureGenerationRunEntity entity =
                new ScientificFeatureGenerationRunMapper()
                        .toEntity(
                                domain,
                                vector
                        );

        when(vectorRepository.findById("SFV-001"))
                .thenReturn(Optional.of(vector));

        when(runRepository.save(any()))
                .thenReturn(entity);

        ScientificFeatureGenerationRun result =
                adapter.save(domain);

        assertThat(result.isCompleted())
                .isTrue();

        assertThat(result.featureVectorId())
                .isEqualTo(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        )
                );

        verify(vectorRepository)
                .findById("SFV-001");
    }

    @Test
    void shouldRejectMissingVectorForCompletedRun() {
        ScientificFeatureGenerationRun domain =
                startedRun()
                        .complete(
                                new ScientificFeatureVectorId(
                                        "SFV-MISSING"
                                ),
                                CUTOFF.plusSeconds(5)
                        );

        when(
                vectorRepository.findById(
                        "SFV-MISSING"
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                adapter.save(domain)
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "SFV-MISSING"
                );
    }

    @Test
    void shouldFindRunById() {
        ScientificFeatureGenerationRun domain =
                startedRun();

        ScientificFeatureGenerationRunEntity entity =
                new ScientificFeatureGenerationRunMapper()
                        .toEntity(
                                domain,
                                null
                        );

        when(runRepository.findById("SFGR-001"))
                .thenReturn(Optional.of(entity));

        assertThat(
                adapter.findById(
                        new ScientificFeatureGenerationRunId(
                                "SFGR-001"
                        )
                )
        ).isPresent();
    }

    private ScientificFeatureGenerationRun startedRun() {
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
                CUTOFF.plusSeconds(1),
                1
        );
    }

    private ScientificFeatureVectorEntity vectorEntity() {
        return new ScientificFeatureVectorEntity(
                "SFV-001",
                "ST-001",
                "ILP_SCIENTIFIC_BASELINE_V1",
                "SCIENTIFIC_FEATURE_GENERATOR_V1",
                CUTOFF,
                CUTOFF.plusSeconds(2),
                1,
                ScientificFeatureVectorStatus.COMPLETED,
                "CHECKSUM-001"
        );
    }
}