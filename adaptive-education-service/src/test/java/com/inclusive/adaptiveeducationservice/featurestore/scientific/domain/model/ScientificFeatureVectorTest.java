package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.GeneratorVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificChecksum;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureVectorId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureVectorTest {

    @Test
    void shouldCreateValidVector() {
        ScientificFeatureVector vector =
                vector(
                        List.of(
                                numericItem(
                                        "SFI-CE",
                                        "KOLB_CE",
                                        30.0
                                ),
                                textItem(
                                        "SFI-PROFILE",
                                        "KOLB_PROFILE",
                                        "DIVERGENT"
                                )
                        )
                );

        assertThat(vector.id().value())
                .isEqualTo("SFV-001");

        assertThat(vector.participantId().value())
                .isEqualTo("ST-001");

        assertThat(vector.featureCount())
                .isEqualTo(2);

        assertThat(
                vector.containsFeature(
                        new FeatureCode("KOLB_CE")
                )
        ).isTrue();
    }

    @Test
    void shouldFindAndRequireFeatureByCode() {
        ScientificFeatureVector vector =
                vector(
                        List.of(
                                numericItem(
                                        "SFI-CE",
                                        "KOLB_CE",
                                        30.0
                                )
                        )
                );

        assertThat(
                vector.findFeature(
                        new FeatureCode("kolb_ce")
                )
        ).isPresent();

        assertThat(
                vector.requireFeature(
                        new FeatureCode("KOLB_CE")
                ).id()
        ).isEqualTo("SFI-CE");
    }

    @Test
    void shouldRejectDuplicateFeatureCodes() {
        ScientificFeatureItem first =
                numericItem(
                        "SFI-ONE",
                        "KOLB_CE",
                        30.0
                );

        ScientificFeatureItem duplicate =
                numericItem(
                        "SFI-TWO",
                        "kolb_ce",
                        31.0
                );

        assertThatThrownBy(() ->
                vector(
                        List.of(
                                first,
                                duplicate
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "Duplicate feature code"
                );
    }

    @Test
    void shouldRejectEmptyItems() {
        assertThatThrownBy(() ->
                vector(List.of())
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "items must not be empty"
                );
    }

    @Test
    void shouldRejectNullItem() {
        List<ScientificFeatureItem> items =
                new ArrayList<>();

        items.add(null);

        assertThatThrownBy(() ->
                vector(items)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "feature item"
                );
    }

    @Test
    void shouldExposeImmutableItems() {
        ScientificFeatureVector vector =
                vector(
                        List.of(
                                numericItem(
                                        "SFI-CE",
                                        "KOLB_CE",
                                        30.0
                                )
                        )
                );

        assertThatThrownBy(() ->
                vector.items().add(
                        textItem(
                                "SFI-PROFILE",
                                "KOLB_PROFILE",
                                "DIVERGENT"
                        )
                )
        )
                .isInstanceOf(
                        UnsupportedOperationException.class
                );
    }

    @Test
    void shouldRejectGeneratedAtBeforeCutoff() {
        Instant cutoff =
                Instant.parse(
                        "2026-07-26T20:00:00Z"
                );

        assertThatThrownBy(() ->
                new ScientificFeatureVector(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        ),
                        new ParticipantId("ST-001"),
                        new FeatureSetVersion(
                                "ILP_SCIENTIFIC_BASELINE_V1"
                        ),
                        new GeneratorVersion(
                                "SCIENTIFIC_FEATURE_GENERATOR_V1"
                        ),
                        cutoff,
                        cutoff.minusSeconds(1),
                        1,
                        new ScientificChecksum(
                                "CHECKSUM-001"
                        ),
                        List.of(
                                numericItem(
                                        "SFI-CE",
                                        "KOLB_CE",
                                        30.0
                                )
                        )
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
        Instant cutoff =
                Instant.parse(
                        "2026-07-26T20:00:00Z"
                );

        assertThatThrownBy(() ->
                new ScientificFeatureVector(
                        new ScientificFeatureVectorId(
                                "SFV-001"
                        ),
                        new ParticipantId("ST-001"),
                        new FeatureSetVersion(
                                "ILP_SCIENTIFIC_BASELINE_V1"
                        ),
                        new GeneratorVersion(
                                "SCIENTIFIC_FEATURE_GENERATOR_V1"
                        ),
                        cutoff,
                        cutoff.plusSeconds(1),
                        -1,
                        new ScientificChecksum(
                                "CHECKSUM-001"
                        ),
                        List.of(
                                numericItem(
                                        "SFI-CE",
                                        "KOLB_CE",
                                        30.0
                                )
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "sourceObservationCount"
                );
    }

    @Test
    void shouldRejectMissingFeature() {
        ScientificFeatureVector vector =
                vector(
                        List.of(
                                numericItem(
                                        "SFI-CE",
                                        "KOLB_CE",
                                        30.0
                                )
                        )
                );

        assertThatThrownBy(() ->
                vector.requireFeature(
                        new FeatureCode("KOLB_RO")
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "Feature not found"
                );
    }

    private ScientificFeatureVector vector(
            List<ScientificFeatureItem> items
    ) {
        Instant cutoff =
                Instant.parse(
                        "2026-07-26T20:00:00Z"
                );

        return new ScientificFeatureVector(
                new ScientificFeatureVectorId(
                        "SFV-001"
                ),
                new ParticipantId("ST-001"),
                new FeatureSetVersion(
                        "ILP_SCIENTIFIC_BASELINE_V1"
                ),
                new GeneratorVersion(
                        "SCIENTIFIC_FEATURE_GENERATOR_V1"
                ),
                cutoff,
                cutoff.plusSeconds(1),
                1,
                new ScientificChecksum(
                        "CHECKSUM-001"
                ),
                items
        );
    }

    private ScientificFeatureItem numericItem(
            String id,
            String code,
            double value
    ) {
        return new ScientificFeatureItem(
                id,
                new FeatureCode(code),
                FeatureValue.numeric(value),
                "KOLB_V1",
                "ADMIN-001"
        );
    }

    private ScientificFeatureItem textItem(
            String id,
            String code,
            String value
    ) {
        return new ScientificFeatureItem(
                id,
                new FeatureCode(code),
                FeatureValue.text(value),
                "KOLB_V1",
                "ADMIN-001"
        );
    }
}