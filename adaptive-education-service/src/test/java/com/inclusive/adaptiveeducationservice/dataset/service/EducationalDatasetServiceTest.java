package com.inclusive.adaptiveeducationservice.dataset.service;

import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EducationalDatasetServiceTest {

    private final StudentFeatureRepository repository =
            mock(StudentFeatureRepository.class);

    private final EducationalDatasetService service =
            new EducationalDatasetService(repository);

    @Test
    void shouldBuildTrainingSnapshotFromStudentFeatures() {
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

        var snapshot = service.buildTrainingSnapshot();

        assertThat(snapshot).hasSize(1);
        assertThat(snapshot.get(0).studentId()).isEqualTo("ST-001");
        assertThat(snapshot.get(0).kolbStyle()).isEqualTo("CONVERGENT");
    }
}