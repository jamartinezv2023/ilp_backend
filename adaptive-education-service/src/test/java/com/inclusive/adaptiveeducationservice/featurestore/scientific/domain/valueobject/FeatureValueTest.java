package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeatureValueTest {

    @Test
    void shouldCreateTypedValues() {
        FeatureValue numeric =
                FeatureValue.numeric(30.0);

        FeatureValue text =
                FeatureValue.text(" DIVERGENT ");

        FeatureValue bool =
                FeatureValue.bool(false);

        assertThat(numeric.dataType())
                .isEqualTo(
                        FeatureValue.DataType.NUMERIC
                );

        assertThat(
                (
                        (FeatureValue.NumericValue)
                                numeric
                ).value()
        ).isEqualTo(30.0);

        assertThat(text.dataType())
                .isEqualTo(
                        FeatureValue.DataType.TEXT
                );

        assertThat(
                (
                        (FeatureValue.TextValue)
                                text
                ).value()
        ).isEqualTo("DIVERGENT");

        assertThat(bool.dataType())
                .isEqualTo(
                        FeatureValue.DataType.BOOLEAN
                );

        assertThat(
                (
                        (FeatureValue.BooleanValue)
                                bool
                ).value()
        ).isFalse();
    }

    @Test
    void shouldRejectNonFiniteNumericValues() {
        assertThatThrownBy(() ->
                FeatureValue.numeric(
                        Double.NaN
                )
        ).isInstanceOf(
                IllegalArgumentException.class
        );

        assertThatThrownBy(() ->
                FeatureValue.numeric(
                        Double.POSITIVE_INFINITY
                )
        ).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    void shouldRejectBlankTextValue() {
        assertThatThrownBy(() ->
                FeatureValue.text(" ")
        ).isInstanceOf(
                IllegalArgumentException.class
        );
    }
}