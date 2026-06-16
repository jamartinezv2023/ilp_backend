package com.inclusive.adaptiveeducationservice.mlpipeline.service;

import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KolbBaselineMlServiceTest {

    private final StudentFeatureRepository repository =
            mock(StudentFeatureRepository.class);

    private final KolbBaselineMlService service =
            new KolbBaselineMlService(repository);

    @Test
    void shouldEvaluateMajorityClassBaseline() {

        Instant now = Instant.now();

        when(repository.findAll())
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
                                        12,
                                        22,
                                        32,
                                        42,
                                        "DIVERGENT"
                                )
                        )
                );

        var result = service.evaluateBaseline();

        assertThat(result.totalRows())
                .isEqualTo(3);

        assertThat(result.baselineStrategy())
                .isEqualTo("MAJORITY_CLASS_BASELINE");

        assertThat(result.predictedMajorityClass())
                .isEqualTo("CONVERGENT");

        assertThat(
                result.classDistribution()
                        .get("CONVERGENT")
        ).isEqualTo(2L);
    }
}