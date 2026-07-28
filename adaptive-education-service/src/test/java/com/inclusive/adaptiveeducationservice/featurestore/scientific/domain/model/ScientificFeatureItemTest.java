package com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureItemTest {

    @Test
    void shouldCreateNumericFeatureItem() {
        ScientificFeatureItem item =
                new ScientificFeatureItem(
                        "SFI-001",
                        new FeatureCode("kolb_ce"),
                        FeatureValue.numeric(30.0),
                        " KOLB_V1 ",
                        " ADMIN-001 "
                );

        assertThat(item.id())
                .isEqualTo("SFI-001");

        assertThat(item.featureCode().value())
                .isEqualTo("KOLB_CE");

        assertThat(item.value())
                .isEqualTo(
                        FeatureValue.numeric(30.0)
                );

        assertThat(item.sourceAssessmentCode())
                .isEqualTo("KOLB_V1");

        assertThat(item.sourceAdministrationId())
                .isEqualTo("ADMIN-001");
    }

    @Test
    void shouldSupportTextAndBooleanValues() {
        ScientificFeatureItem textItem =
                new ScientificFeatureItem(
                        "SFI-TEXT",
                        new FeatureCode("KOLB_PROFILE"),
                        FeatureValue.text("DIVERGENT"),
                        null,
                        null
                );

        ScientificFeatureItem booleanItem =
                new ScientificFeatureItem(
                        "SFI-BOOLEAN",
                        new FeatureCode("PROFILE_CHANGED"),
                        FeatureValue.bool(false),
                        null,
                        null
                );

        assertThat(textItem.value())
                .isInstanceOf(
                        FeatureValue.TextValue.class
                );

        assertThat(booleanItem.value())
                .isInstanceOf(
                        FeatureValue.BooleanValue.class
                );
    }

    @Test
    void shouldNormalizeBlankOptionalSourcesToNull() {
        ScientificFeatureItem item =
                new ScientificFeatureItem(
                        "SFI-001",
                        new FeatureCode("KOLB_CE"),
                        FeatureValue.numeric(30.0),
                        " ",
                        " "
                );

        assertThat(item.sourceAssessmentCode())
                .isNull();

        assertThat(item.sourceAdministrationId())
                .isNull();
    }

    @Test
    void shouldRejectBlankId() {
        assertThatThrownBy(() ->
                new ScientificFeatureItem(
                        " ",
                        new FeatureCode("KOLB_CE"),
                        FeatureValue.numeric(30.0),
                        null,
                        null
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining("id");
    }

    @Test
    void shouldRejectNullFeatureCode() {
        assertThatThrownBy(() ->
                new ScientificFeatureItem(
                        "SFI-001",
                        null,
                        FeatureValue.numeric(30.0),
                        null,
                        null
                )
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "featureCode"
                );
    }

    @Test
    void shouldRejectNullFeatureValue() {
        assertThatThrownBy(() ->
                new ScientificFeatureItem(
                        "SFI-001",
                        new FeatureCode("KOLB_CE"),
                        null,
                        null,
                        null
                )
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "featureValue"
                );
    }

    @Test
    void shouldUseRecordEquality() {
        ScientificFeatureItem first =
                new ScientificFeatureItem(
                        "SFI-001",
                        new FeatureCode("KOLB_CE"),
                        FeatureValue.numeric(30.0),
                        "KOLB_V1",
                        "ADMIN-001"
                );

        ScientificFeatureItem same =
                new ScientificFeatureItem(
                        "SFI-001",
                        new FeatureCode("KOLB_CE"),
                        FeatureValue.numeric(30.0),
                        "KOLB_V1",
                        "ADMIN-001"
                );

        assertThat(first)
                .isEqualTo(same);

        assertThat(first.hashCode())
                .isEqualTo(same.hashCode());
    }
}