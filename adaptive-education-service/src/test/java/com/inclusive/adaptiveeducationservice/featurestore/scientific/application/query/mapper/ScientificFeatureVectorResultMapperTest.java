package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query.mapper;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureItemResult;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureVectorResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ScientificFeatureVectorResultMapperTest {

    private static final Instant FEATURE_CUTOFF_AT =
            Instant.parse(
                    "2026-08-02T12:00:00Z"
            );

    private static final Instant GENERATED_AT =
            Instant.parse(
                    "2026-08-02T12:00:05Z"
            );

    private final ScientificFeatureVectorResultMapper mapper =
            new ScientificFeatureVectorResultMapper();

    @Test
    void shouldMapCompleteScientificVector() {
        ScientificFeatureVectorResult result =
                mapper.toResult(
                        vector()
                );

        assertThat(result.vectorId())
                .isEqualTo(
                        "VECTOR-MAPPER-001"
                );

        assertThat(result.participantId())
                .isEqualTo(
                        "PARTICIPANT-MAPPER-001"
                );

        assertThat(result.featureSetVersion())
                .isEqualTo(
                        "FEATURES-V1"
                );

        assertThat(result.generatorVersion())
                .isEqualTo(
                        "GENERATOR-V1"
                );

        assertThat(result.featureCutoffAt())
                .isEqualTo(
                        FEATURE_CUTOFF_AT
                );

        assertThat(result.generatedAt())
                .isEqualTo(
                        GENERATED_AT
                );

        assertThat(result.sourceObservationCount())
                .isEqualTo(3);

        assertThat(result.checksum())
                .isEqualTo(
                        "CHECKSUM-MAPPER-001"
                );

        assertThat(result.featureCount())
                .isEqualTo(3);
    }

    @Test
    void shouldMapNumericFeatureWithoutInformationLoss() {
        ScientificFeatureItemResult result =
                mapper.toItemResult(
                        numericItem()
                );

        assertThat(result.itemId())
                .isEqualTo(
                        "ITEM-NUMERIC"
                );

        assertThat(result.featureCode())
                .isEqualTo(
                        "KOLB_CE"
                );

        assertThat(result.dataType())
                .isEqualTo(
                        ScientificFeatureItemResult.DataType.NUMERIC
                );

        assertThat(result.numericValue())
                .isEqualTo(25.5);

        assertThat(result.textValue())
                .isNull();

        assertThat(result.booleanValue())
                .isNull();

        assertThat(result.sourceAssessmentCode())
                .isEqualTo(
                        "KOLB"
                );

        assertThat(result.sourceAdministrationId())
                .isEqualTo(
                        "ADMIN-001"
                );
    }

    @Test
    void shouldMapTextFeatureWithoutInformationLoss() {
        ScientificFeatureItemResult result =
                mapper.toItemResult(
                        textItem()
                );

        assertThat(result.itemId())
                .isEqualTo(
                        "ITEM-TEXT"
                );

        assertThat(result.featureCode())
                .isEqualTo(
                        "LEARNING_STYLE"
                );

        assertThat(result.dataType())
                .isEqualTo(
                        ScientificFeatureItemResult.DataType.TEXT
                );

        assertThat(result.textValue())
                .isEqualTo(
                        "DIVERGING"
                );

        assertThat(result.numericValue())
                .isNull();

        assertThat(result.booleanValue())
                .isNull();
    }

    @Test
    void shouldMapBooleanFeatureWithoutInformationLoss() {
        ScientificFeatureItemResult result =
                mapper.toItemResult(
                        booleanItem()
                );

        assertThat(result.itemId())
                .isEqualTo(
                        "ITEM-BOOLEAN"
                );

        assertThat(result.featureCode())
                .isEqualTo(
                        "NEEDS_SUPPORT"
                );

        assertThat(result.dataType())
                .isEqualTo(
                        ScientificFeatureItemResult.DataType.BOOLEAN
                );

        assertThat(result.booleanValue())
                .isTrue();

        assertThat(result.numericValue())
                .isNull();

        assertThat(result.textValue())
                .isNull();

        assertThat(result.sourceAssessmentCode())
                .isNull();

        assertThat(result.sourceAdministrationId())
                .isNull();
    }

    @Test
    void shouldPreserveDomainFeatureOrder() {
        ScientificFeatureVector vector =
                vector();

        ScientificFeatureVectorResult result =
                mapper.toResult(
                        vector
                );

        List<String> expectedFeatureCodes =
                vector.items()
                        .stream()
                        .map(item ->
                                item.featureCode().value()
                        )
                        .toList();

        List<String> actualFeatureCodes =
                result.features()
                        .stream()
                        .map(
                                ScientificFeatureItemResult::featureCode
                        )
                        .toList();

        assertThat(actualFeatureCodes)
                .containsExactlyElementsOf(
                        expectedFeatureCodes
                );
    }

    @Test
    void shouldReturnImmutableFeatureCollection() {
        ScientificFeatureVectorResult result =
                mapper.toResult(
                        vector()
                );

        assertThat(result.features())
                .isUnmodifiable();
    }

    @Test
    void shouldRejectNullVector() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        mapper.toResult(null)
                )
                .withMessageContaining(
                        "vector"
                );
    }

    @Test
    void shouldRejectNullItem() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        mapper.toItemResult(null)
                )
                .withMessageContaining(
                        "item"
                );
    }

    private ScientificFeatureVector vector() {
        return new ScientificFeatureVector(
                new ScientificFeatureVectorId(
                        "VECTOR-MAPPER-001"
                ),
                new ParticipantId(
                        "PARTICIPANT-MAPPER-001"
                ),
                new FeatureSetVersion(
                        "FEATURES-V1"
                ),
                new GeneratorVersion(
                        "GENERATOR-V1"
                ),
                FEATURE_CUTOFF_AT,
                GENERATED_AT,
                3,
                new ScientificChecksum(
                        "CHECKSUM-MAPPER-001"
                ),
                List.of(
                        numericItem(),
                        textItem(),
                        booleanItem()
                )
        );
    }

    private ScientificFeatureItem numericItem() {
        return new ScientificFeatureItem(
                "ITEM-NUMERIC",
                new FeatureCode(
                        "KOLB_CE"
                ),
                FeatureValue.numeric(
                        25.5
                ),
                "KOLB",
                "ADMIN-001"
        );
    }

    private ScientificFeatureItem textItem() {
        return new ScientificFeatureItem(
                "ITEM-TEXT",
                new FeatureCode(
                        "LEARNING_STYLE"
                ),
                FeatureValue.text(
                        "DIVERGING"
                ),
                "KOLB",
                "ADMIN-001"
        );
    }

    private ScientificFeatureItem booleanItem() {
        return new ScientificFeatureItem(
                "ITEM-BOOLEAN",
                new FeatureCode(
                        "NEEDS_SUPPORT"
                ),
                FeatureValue.bool(
                        true
                ),
                null,
                null
        );
    }
}
