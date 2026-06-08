package com.inclusive.adaptiveeducationservice.featurestore.service;

import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import com.inclusive.adaptiveeducationservice.featurestore.extractor.KolbFeatureExtractor;
import com.inclusive.adaptiveeducationservice.featurestore.repository.StudentFeatureRepository;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeatureStoreServiceTest {

    private final StudentFeatureRepository repository =
            mock(StudentFeatureRepository.class);

    private final AssessmentResponseRepository responseRepository =
            mock(AssessmentResponseRepository.class);

    private final KolbFeatureExtractor extractor =
            mock(KolbFeatureExtractor.class);

    private final FeatureStoreService service =
            new FeatureStoreService(
                    repository,
                    responseRepository,
                    extractor
            );

    @Test
    void shouldRebuildStudentFeatures() {

        when(extractor.extract(any()))
                .thenReturn(
                        new KolbFeatureExtractor.KolbFeatures(
                                10,
                                20,
                                30,
                                40,
                                "CONVERGENT"
                        )
                );

        when(repository.save(any(StudentFeatureEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result =
                service.rebuildStudentFeatures("ST-001");

        assertThat(result.studentId())
                .isEqualTo("ST-001");

        assertThat(result.kolbStyle())
                .isEqualTo("CONVERGENT");
    }
}
