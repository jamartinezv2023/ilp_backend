package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureGeneratorTest {

    private static final Instant CUTOFF =
            Instant.parse(
                    "2026-07-28T12:00:00Z"
            );

    private static final Instant GENERATED_AT =
            CUTOFF.plusSeconds(5);

    private final ScientificFeatureGenerator generator =
            request ->
                    new ScientificFeatureVector(
                            request.vectorId(),
                            request.participantId(),
                            request.featureSetVersion(),
                            request.generatorVersion(),
                            request.featureCutoffAt(),
                            request.generatedAt(),
                            request.inputObservationCount(),
                            request.checksum(),
                            request.extractedFeatures()
                    );

    @Test
    void shouldGenerateVectorFromValidatedRequest() {
        ScientificFeatureGenerationRequest request =
                validRequest(
                        List.of(featureItem())
                );

        ScientificFeatureVector result =
                generator.generate(request);

        assertThat(result.id())
                .isEqualTo(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        )
                );

        assertThat(result.participantId())
                .isEqualTo(
                        new ParticipantId("ST-001")
                );

        assertThat(result.featureSetVersion())
                .isEqualTo(
                        new FeatureSetVersion(
                                "ILP_SCIENTIFIC_BASELINE_V1"
                        )
                );

        assertThat(result.generatorVersion())
                .isEqualTo(
                        new GeneratorVersion(
                                "SCIENTIFIC_FEATURE_GENERATOR_V1"
                        )
                );

        assertThat(result.featureCutoffAt())
                .isEqualTo(CUTOFF);

        assertThat(result.generatedAt())
                .isEqualTo(GENERATED_AT);

        assertThat(result.inputObservationCount())
                .isEqualTo(1);

        assertThat(result.featureCount())
                .isEqualTo(1);

        assertThat(result.checksum())
                .isEqualTo(
                        new ScientificChecksum(
                                "CHECKSUM-001"
                        )
                );
    }

    @Test
    void shouldDefensivelyCopyExtractedFeatures() {
        List<ScientificFeatureItem> mutableFeatures =
                new ArrayList<>();

        mutableFeatures.add(featureItem());

        ScientificFeatureGenerationRequest request =
                validRequest(mutableFeatures);

        mutableFeatures.clear();

        assertThat(request.extractedFeatures())
                .hasSize(1);

        assertThatThrownBy(() ->
                request.extractedFeatures()
                        .add(featureItem())
        )
                .isInstanceOf(
                        UnsupportedOperationException.class
                );
    }

    @Test
    void shouldExposeFeatureCount() {
        ScientificFeatureGenerationRequest request =
                validRequest(
                        List.of(featureItem())
                );

        assertThat(request.featureCount())
                .isEqualTo(1);
    }

    @Test
    void shouldRejectGenerationBeforeCutoff() {
        assertThatThrownBy(() ->
                new ScientificFeatureGenerationRequest(
                        vectorId(),
                        participantId(),
                        featureSetVersion(),
                        generatorVersion(),
                        CUTOFF,
                        CUTOFF.minusSeconds(1),
                        1,
                        checksum(),
                        List.of(featureItem())
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "generatedAt"
                );
    }

    @Test
    void shouldRejectNegativeObservationCount() {
        assertThatThrownBy(() ->
                new ScientificFeatureGenerationRequest(
                        vectorId(),
                        participantId(),
                        featureSetVersion(),
                        generatorVersion(),
                        CUTOFF,
                        GENERATED_AT,
                        -1,
                        checksum(),
                        List.of(featureItem())
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "non-negative"
                );
    }

    @Test
    void shouldRejectEmptyFeatureCollection() {
        assertThatThrownBy(() ->
                validRequest(List.of())
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "At least one"
                );
    }

    @Test
    void shouldRejectNullFeatureElement() {
        List<ScientificFeatureItem> features =
                new ArrayList<>();

        features.add(null);

        assertThatThrownBy(() ->
                validRequest(features)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "null elements"
                );
    }

    @Test
    void shouldRejectNullParticipant() {
        assertThatThrownBy(() ->
                new ScientificFeatureGenerationRequest(
                        vectorId(),
                        null,
                        featureSetVersion(),
                        generatorVersion(),
                        CUTOFF,
                        GENERATED_AT,
                        1,
                        checksum(),
                        List.of(featureItem())
                )
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "participantId"
                );
    }

    private ScientificFeatureGenerationRequest validRequest(
            List<ScientificFeatureItem> features
    ) {
        return new ScientificFeatureGenerationRequest(
                vectorId(),
                participantId(),
                featureSetVersion(),
                generatorVersion(),
                CUTOFF,
                GENERATED_AT,
                1,
                checksum(),
                features
        );
    }

    private ScientificFeatureItem featureItem() {
        return new ScientificFeatureItem(
                "SFI-001",
                new FeatureCode("KOLB_CE"),
                FeatureValue.numeric(30.0),
                "KOLB_V1",
                "ADMIN-001"
        );
    }

    private ScientificFeatureVectorId vectorId() {
        return new ScientificFeatureVectorId(
                "SFV-001"
        );
    }

    private ParticipantId participantId() {
        return new ParticipantId(
                "ST-001"
        );
    }

    private FeatureSetVersion featureSetVersion() {
        return new FeatureSetVersion(
                "ILP_SCIENTIFIC_BASELINE_V1"
        );
    }

    private GeneratorVersion generatorVersion() {
        return new GeneratorVersion(
                "SCIENTIFIC_FEATURE_GENERATOR_V1"
        );
    }

    private ScientificChecksum checksum() {
        return new ScientificChecksum(
                "CHECKSUM-001"
        );
    }
}