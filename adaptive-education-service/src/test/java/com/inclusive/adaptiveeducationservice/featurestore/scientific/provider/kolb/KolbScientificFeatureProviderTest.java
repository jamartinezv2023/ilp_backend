package com.inclusive.adaptiveeducationservice.featurestore.scientific.provider.kolb;

import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.repository.KolbAssessmentResultRepository;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KolbScientificFeatureProviderTest {

    private static final String PARTICIPANT_ID = "STUDENT-001";
    private static final String ASSESSMENT_ID = "KOLB-RESULT-001";

    private static final Instant CUTOFF_AT =
            Instant.parse("2026-07-29T20:00:00Z");

    private static final Instant RESULT_CREATED_AT =
            Instant.parse("2026-07-28T18:00:00Z");

    @Mock
    private KolbAssessmentResultRepository resultRepository;

    private KolbScientificFeatureProvider provider;

    @BeforeEach
    void setUp() {
        provider = new KolbScientificFeatureProvider(
                resultRepository
        );
    }

    @Test
    void shouldProvideSevenScientificFeaturesFromLatestEligibleResult() {
        var request = request();

        when(
                resultRepository
                        .findFirstByStudentIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                                PARTICIPANT_ID,
                                CUTOFF_AT
                        )
        ).thenReturn(Optional.of(result()));

        List<ScientificFeatureItem> features =
                provider.provide(request);

        assertEquals(7, features.size());

        Map<String, ScientificFeatureItem> featuresByCode =
                features.stream()
                        .collect(
                                Collectors.toMap(
                                        feature ->
                                                feature.featureCode().value(),
                                        Function.identity()
                                )
                        );

        assertNumericFeature(
                featuresByCode,
                KolbScientificFeatureProvider.CE_SCORE_CODE,
                18.0
        );

        assertNumericFeature(
                featuresByCode,
                KolbScientificFeatureProvider.RO_SCORE_CODE,
                24.0
        );

        assertNumericFeature(
                featuresByCode,
                KolbScientificFeatureProvider.AC_SCORE_CODE,
                32.0
        );

        assertNumericFeature(
                featuresByCode,
                KolbScientificFeatureProvider.AE_SCORE_CODE,
                29.0
        );

        assertNumericFeature(
                featuresByCode,
                KolbScientificFeatureProvider.AC_MINUS_CE_CODE,
                14.0
        );

        assertNumericFeature(
                featuresByCode,
                KolbScientificFeatureProvider.AE_MINUS_RO_CODE,
                5.0
        );

        assertTextFeature(
                featuresByCode,
                KolbScientificFeatureProvider.LEARNING_STYLE_CODE,
                "CONVERGING"
        );

        features.forEach(feature -> {
            assertEquals(
                    KolbScientificFeatureProvider.SOURCE_ASSESSMENT_CODE,
                    feature.sourceAssessmentCode()
            );

            assertEquals(
                    ASSESSMENT_ID,
                    feature.sourceAdministrationId()
            );

            assertEquals(
                    ASSESSMENT_ID
                            + "-"
                            + feature.featureCode().value(),
                    feature.id()
            );
        });

        verify(resultRepository)
                .findFirstByStudentIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                        PARTICIPANT_ID,
                        CUTOFF_AT
                );
    }

    @Test
    void shouldReturnEmptyListWhenNoEligibleKolbResultExists() {
        var request = request();

        when(
                resultRepository
                        .findFirstByStudentIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                                PARTICIPANT_ID,
                                CUTOFF_AT
                        )
        ).thenReturn(Optional.empty());

        List<ScientificFeatureItem> features =
                provider.provide(request);

        assertNotNull(features);
        assertTrue(features.isEmpty());

        verify(resultRepository)
                .findFirstByStudentIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                        PARTICIPANT_ID,
                        CUTOFF_AT
                );
    }

    @Test
    void shouldRejectNullRequest() {
        NullPointerException exception =
                assertThrows(
                        NullPointerException.class,
                        () -> provider.provide(null)
                );

        assertEquals(
                "request is required",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullRepository() {
        NullPointerException exception =
                assertThrows(
                        NullPointerException.class,
                        () -> new KolbScientificFeatureProvider(null)
                );

        assertEquals(
                "resultRepository is required",
                exception.getMessage()
        );
    }

    private void assertNumericFeature(
            Map<String, ScientificFeatureItem> featuresByCode,
            String code,
            double expectedValue
    ) {
        ScientificFeatureItem feature =
                featuresByCode.get(code);

        assertNotNull(
                feature,
                "Missing feature: " + code
        );

        assertEquals(
                new FeatureCode(code),
                feature.featureCode()
        );

        assertEquals(
                FeatureValue.numeric(expectedValue),
                feature.value()
        );
    }

    private void assertTextFeature(
            Map<String, ScientificFeatureItem> featuresByCode,
            String code,
            String expectedValue
    ) {
        ScientificFeatureItem feature =
                featuresByCode.get(code);

        assertNotNull(
                feature,
                "Missing feature: " + code
        );

        assertEquals(
                new FeatureCode(code),
                feature.featureCode()
        );

        assertEquals(
                FeatureValue.text(expectedValue),
                feature.value()
        );
    }

    private KolbAssessmentResultEntity result() {
        return new KolbAssessmentResultEntity(
                ASSESSMENT_ID,
                PARTICIPANT_ID,
                18,
                24,
                32,
                29,
                "CONVERGING",
                "KOLB_BASELINE_V1",
                RESULT_CREATED_AT,
                List.of(1, 2, 3, 4)
        );
    }

    private ScientificFeatureGenerationRequest request() {
        return new ScientificFeatureGenerationRequest(
                new ScientificFeatureVectorId(
                        "VECTOR-KOLB-001"
                ),
                new ParticipantId(
                        PARTICIPANT_ID
                ),
                new FeatureSetVersion(
                        "KOLB-FEATURES-V1"
                ),
                new GeneratorVersion(
                        "KOLB-PROVIDER-V1"
                ),
                CUTOFF_AT,
                CUTOFF_AT.plusSeconds(60),
                1,
                new ScientificChecksum(
                        "sha256:0123456789abcdef"
                ),
                List.of(
                        seedFeature()
                )
        );
    }

    private ScientificFeatureItem seedFeature() {
        return new ScientificFeatureItem(
                "SEED-FEATURE-001",
                new FeatureCode("SEED_FEATURE"),
                FeatureValue.numeric(1.0),
                "SEED",
                "SEED-ADMINISTRATION-001"
        );
    }
}