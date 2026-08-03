package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureVectorResultTest {

    private static final Instant FEATURE_CUTOFF_AT =
            Instant.parse(
                    "2026-08-02T12:00:00Z"
            );

    private static final Instant GENERATED_AT =
            Instant.parse(
                    "2026-08-02T12:00:05Z"
            );

    @Test
    void shouldCreateCompleteVectorResult() {
        ScientificFeatureVectorResult result =
                result(
                        List.of(feature())
                );

        assertThat(result.vectorId())
                .isEqualTo("VECTOR-001");

        assertThat(result.participantId())
                .isEqualTo("PARTICIPANT-001");

        assertThat(result.featureSetVersion())
                .isEqualTo("FEATURES-V1");

        assertThat(result.generatorVersion())
                .isEqualTo("GENERATOR-V1");

        assertThat(result.featureCutoffAt())
                .isEqualTo(FEATURE_CUTOFF_AT);

        assertThat(result.generatedAt())
                .isEqualTo(GENERATED_AT);

        assertThat(result.sourceObservationCount())
                .isEqualTo(1);

        assertThat(result.checksum())
                .isEqualTo("CHECKSUM-001");

        assertThat(result.featureCount())
                .isEqualTo(1);
    }

    @Test
    void shouldTrimRequiredTextFields() {
        ScientificFeatureVectorResult result =
                new ScientificFeatureVectorResult(
                        " VECTOR-001 ",
                        " PARTICIPANT-001 ",
                        " FEATURES-V1 ",
                        " GENERATOR-V1 ",
                        FEATURE_CUTOFF_AT,
                        GENERATED_AT,
                        1,
                        " CHECKSUM-001 ",
                        List.of(feature())
                );

        assertThat(result.vectorId())
                .isEqualTo("VECTOR-001");

        assertThat(result.participantId())
                .isEqualTo("PARTICIPANT-001");

        assertThat(result.featureSetVersion())
                .isEqualTo("FEATURES-V1");

        assertThat(result.generatorVersion())
                .isEqualTo("GENERATOR-V1");

        assertThat(result.checksum())
                .isEqualTo("CHECKSUM-001");
    }

    @Test
    void shouldDefensivelyCopyFeatureCollection() {
        List<ScientificFeatureItemResult> source =
                new ArrayList<>();

        source.add(feature());

        ScientificFeatureVectorResult result =
                result(source);

        source.clear();

        assertThat(result.features())
                .containsExactly(feature());

        assertThat(result.featureCount())
                .isEqualTo(1);
    }

    @Test
    void shouldExposeUnmodifiableFeatureCollection() {
        ScientificFeatureVectorResult result =
                result(
                        List.of(feature())
                );

        assertThatThrownBy(() ->
                result.features().add(feature())
        )
                .isInstanceOf(
                        UnsupportedOperationException.class
                );
    }

    @Test
    void shouldDeriveFeatureCountFromCollection() {
        ScientificFeatureVectorResult result =
                result(
                        List.of(
                                feature(),
                                ScientificFeatureItemResult.text(
                                        "ITEM-002",
                                        "LEARNING_STYLE",
                                        "DIVERGING",
                                        "KOLB",
                                        "ADMIN-001"
                                )
                        )
                );

        assertThat(result.featureCount())
                .isEqualTo(2);
    }

    @Test
    void shouldAllowGeneratedAtEqualToCutoff() {
        ScientificFeatureVectorResult result =
                new ScientificFeatureVectorResult(
                        "VECTOR-001",
                        "PARTICIPANT-001",
                        "FEATURES-V1",
                        "GENERATOR-V1",
                        FEATURE_CUTOFF_AT,
                        FEATURE_CUTOFF_AT,
                        0,
                        "CHECKSUM-001",
                        List.of(feature())
                );

        assertThat(result.generatedAt())
                .isEqualTo(FEATURE_CUTOFF_AT);
    }

    @Test
    void shouldRejectGeneratedAtBeforeCutoff() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                "VECTOR-001",
                                "PARTICIPANT-001",
                                "FEATURES-V1",
                                "GENERATOR-V1",
                                FEATURE_CUTOFF_AT,
                                FEATURE_CUTOFF_AT.minusSeconds(1),
                                1,
                                "CHECKSUM-001",
                                List.of(feature())
                        )
                )
                .withMessageContaining(
                        "must not be before"
                );
    }

    @Test
    void shouldRejectNegativeSourceObservationCount() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                "VECTOR-001",
                                "PARTICIPANT-001",
                                "FEATURES-V1",
                                "GENERATOR-V1",
                                FEATURE_CUTOFF_AT,
                                GENERATED_AT,
                                -1,
                                "CHECKSUM-001",
                                List.of(feature())
                        )
                )
                .withMessageContaining(
                        "non-negative"
                );
    }

    @Test
    void shouldRejectNullFeatureCollection() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        result(null)
                )
                .withMessageContaining("features");
    }

    @Test
    void shouldRejectEmptyFeatureCollection() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        result(List.of())
                )
                .withMessageContaining(
                        "must not be empty"
                );
    }

    @Test
    void shouldRejectNullFeatureElement() {
        List<ScientificFeatureItemResult> features =
                new ArrayList<>();

        features.add(null);

        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        result(features)
                )
                .withMessageContaining(
                        "null elements"
                );
    }

    @Test
    void shouldRejectNullVectorId() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                null,
                                "PARTICIPANT-001",
                                "FEATURES-V1",
                                "GENERATOR-V1",
                                FEATURE_CUTOFF_AT,
                                GENERATED_AT,
                                1,
                                "CHECKSUM-001",
                                List.of(feature())
                        )
                )
                .withMessageContaining("vectorId");
    }

    @Test
    void shouldRejectBlankParticipantId() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                "VECTOR-001",
                                " ",
                                "FEATURES-V1",
                                "GENERATOR-V1",
                                FEATURE_CUTOFF_AT,
                                GENERATED_AT,
                                1,
                                "CHECKSUM-001",
                                List.of(feature())
                        )
                )
                .withMessageContaining("participantId");
    }

    @Test
    void shouldRejectNullFeatureSetVersion() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                "VECTOR-001",
                                "PARTICIPANT-001",
                                null,
                                "GENERATOR-V1",
                                FEATURE_CUTOFF_AT,
                                GENERATED_AT,
                                1,
                                "CHECKSUM-001",
                                List.of(feature())
                        )
                )
                .withMessageContaining(
                        "featureSetVersion"
                );
    }

    @Test
    void shouldRejectBlankGeneratorVersion() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                "VECTOR-001",
                                "PARTICIPANT-001",
                                "FEATURES-V1",
                                " ",
                                FEATURE_CUTOFF_AT,
                                GENERATED_AT,
                                1,
                                "CHECKSUM-001",
                                List.of(feature())
                        )
                )
                .withMessageContaining(
                        "generatorVersion"
                );
    }

    @Test
    void shouldRejectNullFeatureCutoffAt() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                "VECTOR-001",
                                "PARTICIPANT-001",
                                "FEATURES-V1",
                                "GENERATOR-V1",
                                null,
                                GENERATED_AT,
                                1,
                                "CHECKSUM-001",
                                List.of(feature())
                        )
                )
                .withMessageContaining(
                        "featureCutoffAt"
                );
    }

    @Test
    void shouldRejectNullGeneratedAt() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                "VECTOR-001",
                                "PARTICIPANT-001",
                                "FEATURES-V1",
                                "GENERATOR-V1",
                                FEATURE_CUTOFF_AT,
                                null,
                                1,
                                "CHECKSUM-001",
                                List.of(feature())
                        )
                )
                .withMessageContaining(
                        "generatedAt"
                );
    }

    @Test
    void shouldRejectBlankChecksum() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureVectorResult(
                                "VECTOR-001",
                                "PARTICIPANT-001",
                                "FEATURES-V1",
                                "GENERATOR-V1",
                                FEATURE_CUTOFF_AT,
                                GENERATED_AT,
                                1,
                                " ",
                                List.of(feature())
                        )
                )
                .withMessageContaining("checksum");
    }

    private ScientificFeatureVectorResult result(
            List<ScientificFeatureItemResult> features
    ) {
        return new ScientificFeatureVectorResult(
                "VECTOR-001",
                "PARTICIPANT-001",
                "FEATURES-V1",
                "GENERATOR-V1",
                FEATURE_CUTOFF_AT,
                GENERATED_AT,
                1,
                "CHECKSUM-001",
                features
        );
    }

    private ScientificFeatureItemResult feature() {
        return ScientificFeatureItemResult.numeric(
                "ITEM-001",
                "KOLB_CE",
                25.0,
                "KOLB",
                "ADMIN-001"
        );
    }
}
