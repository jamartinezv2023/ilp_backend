package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureIdentifiersTest {

    @Test
    void shouldNormalizeIdentifiersAndVersions() {
        assertThat(
                new ParticipantId("  ST-001  ").value()
        ).isEqualTo("ST-001");

        assertThat(
                new ScientificFeatureVectorId(
                        "  SFV-001  "
                ).value()
        ).isEqualTo("SFV-001");

        assertThat(
                new ScientificFeatureGenerationRunId(
                        "  SFGR-001  "
                ).value()
        ).isEqualTo("SFGR-001");

        assertThat(
                new FeatureSetVersion(
                        "  ILP_SCIENTIFIC_BASELINE_V1  "
                ).value()
        ).isEqualTo(
                "ILP_SCIENTIFIC_BASELINE_V1"
        );

        assertThat(
                new GeneratorVersion(
                        "  SCIENTIFIC_FEATURE_GENERATOR_V1  "
                ).value()
        ).isEqualTo(
                "SCIENTIFIC_FEATURE_GENERATOR_V1"
        );
    }

    @Test
    void shouldRejectBlankIdentifiers() {
        assertThatThrownBy(() ->
                new ParticipantId(" ")
        ).isInstanceOf(
                IllegalArgumentException.class
        );

        assertThatThrownBy(() ->
                new ScientificFeatureVectorId(" ")
        ).isInstanceOf(
                IllegalArgumentException.class
        );

        assertThatThrownBy(() ->
                new ScientificFeatureGenerationRunId(" ")
        ).isInstanceOf(
                IllegalArgumentException.class
        );

        assertThatThrownBy(() ->
                new FeatureSetVersion(" ")
        ).isInstanceOf(
                IllegalArgumentException.class
        );

        assertThatThrownBy(() ->
                new GeneratorVersion(" ")
        ).isInstanceOf(
                IllegalArgumentException.class
        );
    }
}