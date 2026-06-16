package com.inclusive.adaptiveeducationservice.mlmodels.service;

import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import com.inclusive.adaptiveeducationservice.mlmodels.repository.MlExperimentRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RandomForestKolbServiceTest {

    private final StudentFeatureRepository featureRepository =
            mock(StudentFeatureRepository.class);

    private final MlExperimentRepository experimentRepository =
            mock(MlExperimentRepository.class);

    private final RandomForestKolbService service =
            new RandomForestKolbService(
                    featureRepository,
                    experimentRepository
            );

    @Test
    void shouldEvaluateRandomForestExperiment() {

        Instant now = Instant.now();

        when(featureRepository.findAll())
                .thenReturn(
                        List.of(
                                new StudentFeatureEntity(
                                        "SF-001",
                                        "ST-001",
                                        now,
                                        10,
                                        20,
                                        30,
                                        40,
                                        "CONVERGENT"
                                ),
                                new StudentFeatureEntity(
                                        "SF-002",
                                        "ST-002",
                                        now.plusSeconds(1),
                                        11,
                                        21,
                                        31,
                                        41,
                                        "CONVERGENT"
                                ),
                                new StudentFeatureEntity(
                                        "SF-003",
                                        "ST-003",
                                        now.plusSeconds(2),
                                        15,
                                        25,
                                        35,
                                        45,
                                        "DIVERGENT"
                                )
                        )
                );

        var response = service.evaluate();

        verify(experimentRepository)
                .save(any());

        assertThat(response.algorithm())
                .isEqualTo("RANDOM_FOREST");

        assertThat(response.target())
                .isEqualTo("KOLB_STYLE");

        assertThat(response.totalRows())
                .isEqualTo(3);

        assertThat(response.featureImportance())
                .containsKeys(
                        "kolbAc",
                        "kolbAe",
                        "kolbRo",
                        "kolbCe"
                );

        assertThat(response.classDistribution())
                .containsKey("CONVERGENT");
    }
}