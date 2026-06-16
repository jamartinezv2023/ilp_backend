package com.inclusive.adaptiveeducationservice.researchanalytics.service;

import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResearchAnalyticsServiceTest {

    private final StudentFeatureRepository repository =
            mock(StudentFeatureRepository.class);

    private final ResearchAnalyticsService service =
            new ResearchAnalyticsService(repository);

    @Test
    void shouldCalculateKolbDistribution() {
        var feature = new StudentFeatureEntity(
                "SF-001",
                "ST-001",
                Instant.now(),
                10,
                20,
                30,
                40,
                "CONVERGENT"
        );

        when(repository.findAll()).thenReturn(List.of(feature));

        var result = service.kolbDistribution();

        assertThat(result.totalFeatureRows()).isEqualTo(1);
        assertThat(result.distribution().get("CONVERGENT")).isEqualTo(1);
    }

    @Test
    void shouldCalculateKolbStatistics() {
        var feature = new StudentFeatureEntity(
                "SF-001",
                "ST-001",
                Instant.now(),
                10,
                20,
                30,
                40,
                "CONVERGENT"
        );

        when(repository.findAll()).thenReturn(List.of(feature));

        var result = service.kolbStatistics();

        assertThat(result.averageCe()).isEqualTo(10.0);
        assertThat(result.averageRo()).isEqualTo(20.0);
        assertThat(result.averageAc()).isEqualTo(30.0);
        assertThat(result.averageAe()).isEqualTo(40.0);
    }

    @Test
    void shouldGenerateResearchSummary() {
        var feature = new StudentFeatureEntity(
                "SF-001",
                "ST-001",
                Instant.now(),
                10,
                20,
                30,
                40,
                "CONVERGENT"
        );

        when(repository.findAll()).thenReturn(List.of(feature));

        var result = service.researchSummary();

        assertThat(result.uniqueStudents()).isEqualTo(1);
        assertThat(result.datasetReadinessStatus())
                .isEqualTo("EARLY_DATA_COLLECTION_STAGE");
    }
}