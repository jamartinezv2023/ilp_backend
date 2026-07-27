package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeatureCodeTest {

    @Test
    void shouldNormalizeFeatureCodeToUppercase() {
        FeatureCode code =
                new FeatureCode("  kolb_ce  ");

        assertThat(code.value())
                .isEqualTo("KOLB_CE");
    }

    @Test
    void shouldAcceptLettersNumbersAndUnderscores() {
        assertThat(
                new FeatureCode(
                        "ASSESSMENT_COUNT_2026"
                ).value()
        ).isEqualTo(
                "ASSESSMENT_COUNT_2026"
        );
    }

    @Test
    void shouldRejectInvalidFeatureCodes() {
        assertThatThrownBy(() ->
                new FeatureCode("1_KOLB")
        ).isInstanceOf(
                IllegalArgumentException.class
        );

        assertThatThrownBy(() ->
                new FeatureCode("KOLB-CE")
        ).isInstanceOf(
                IllegalArgumentException.class
        );

        assertThatThrownBy(() ->
                new FeatureCode(" ")
        ).isInstanceOf(
                IllegalArgumentException.class
        );
    }
}