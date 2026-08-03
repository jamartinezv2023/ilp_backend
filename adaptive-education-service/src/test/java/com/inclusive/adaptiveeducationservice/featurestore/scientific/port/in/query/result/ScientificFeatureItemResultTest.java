package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ScientificFeatureItemResultTest {

    @Test
    void shouldCreateNumericResult() {
        ScientificFeatureItemResult result =
                ScientificFeatureItemResult.numeric(
                        "ITEM-001",
                        "KOLB_CE",
                        25.5,
                        "KOLB",
                        "ADMIN-001"
                );

        assertThat(result.itemId())
                .isEqualTo("ITEM-001");

        assertThat(result.featureCode())
                .isEqualTo("KOLB_CE");

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
                .isEqualTo("KOLB");

        assertThat(result.sourceAdministrationId())
                .isEqualTo("ADMIN-001");
    }

    @Test
    void shouldCreateTextResult() {
        ScientificFeatureItemResult result =
                ScientificFeatureItemResult.text(
                        "ITEM-002",
                        "LEARNING_STYLE",
                        "DIVERGING",
                        "KOLB",
                        "ADMIN-002"
                );

        assertThat(result.dataType())
                .isEqualTo(
                        ScientificFeatureItemResult.DataType.TEXT
                );

        assertThat(result.textValue())
                .isEqualTo("DIVERGING");

        assertThat(result.numericValue())
                .isNull();

        assertThat(result.booleanValue())
                .isNull();
    }

    @Test
    void shouldCreateBooleanResult() {
        ScientificFeatureItemResult result =
                ScientificFeatureItemResult.bool(
                        "ITEM-003",
                        "NEEDS_SUPPORT",
                        true,
                        null,
                        null
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
    }

    @Test
    void shouldTrimRequiredTextFields() {
        ScientificFeatureItemResult result =
                ScientificFeatureItemResult.numeric(
                        " ITEM-001 ",
                        " KOLB_CE ",
                        25.0,
                        " KOLB ",
                        " ADMIN-001 "
                );

        assertThat(result.itemId())
                .isEqualTo("ITEM-001");

        assertThat(result.featureCode())
                .isEqualTo("KOLB_CE");

        assertThat(result.sourceAssessmentCode())
                .isEqualTo("KOLB");

        assertThat(result.sourceAdministrationId())
                .isEqualTo("ADMIN-001");
    }

    @Test
    void shouldConvertBlankOptionalSourcesToNull() {
        ScientificFeatureItemResult result =
                ScientificFeatureItemResult.numeric(
                        "ITEM-001",
                        "KOLB_CE",
                        25.0,
                        " ",
                        ""
                );

        assertThat(result.sourceAssessmentCode())
                .isNull();

        assertThat(result.sourceAdministrationId())
                .isNull();
    }

    @Test
    void shouldRejectNullItemId() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        ScientificFeatureItemResult.numeric(
                                null,
                                "KOLB_CE",
                                25.0,
                                null,
                                null
                        )
                )
                .withMessageContaining("itemId");
    }

    @Test
    void shouldRejectBlankItemId() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        ScientificFeatureItemResult.numeric(
                                " ",
                                "KOLB_CE",
                                25.0,
                                null,
                                null
                        )
                )
                .withMessageContaining("itemId");
    }

    @Test
    void shouldRejectNullFeatureCode() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        ScientificFeatureItemResult.numeric(
                                "ITEM-001",
                                null,
                                25.0,
                                null,
                                null
                        )
                )
                .withMessageContaining("featureCode");
    }

    @Test
    void shouldRejectBlankFeatureCode() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        ScientificFeatureItemResult.numeric(
                                "ITEM-001",
                                " ",
                                25.0,
                                null,
                                null
                        )
                )
                .withMessageContaining("featureCode");
    }

    @Test
    void shouldRejectNullDataType() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new ScientificFeatureItemResult(
                                "ITEM-001",
                                "KOLB_CE",
                                null,
                                25.0,
                                null,
                                null,
                                null,
                                null
                        )
                )
                .withMessageContaining("dataType");
    }

    @Test
    void shouldRejectNaNNumericValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        ScientificFeatureItemResult.numeric(
                                "ITEM-001",
                                "KOLB_CE",
                                Double.NaN,
                                null,
                                null
                        )
                )
                .withMessageContaining("finite");
    }

    @Test
    void shouldRejectPositiveInfinityNumericValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        ScientificFeatureItemResult.numeric(
                                "ITEM-001",
                                "KOLB_CE",
                                Double.POSITIVE_INFINITY,
                                null,
                                null
                        )
                )
                .withMessageContaining("finite");
    }

    @Test
    void shouldRejectNegativeInfinityNumericValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        ScientificFeatureItemResult.numeric(
                                "ITEM-001",
                                "KOLB_CE",
                                Double.NEGATIVE_INFINITY,
                                null,
                                null
                        )
                )
                .withMessageContaining("finite");
    }

    @Test
    void shouldRejectBlankTextValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        ScientificFeatureItemResult.text(
                                "ITEM-001",
                                "LEARNING_STYLE",
                                " ",
                                null,
                                null
                        )
                )
                .withMessageContaining("textValue");
    }

    @Test
    void shouldRejectNumericTypeWithoutNumericValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureItemResult(
                                "ITEM-001",
                                "KOLB_CE",
                                ScientificFeatureItemResult
                                        .DataType.NUMERIC,
                                null,
                                null,
                                null,
                                null,
                                null
                        )
                )
                .withMessageContaining("NUMERIC");
    }

    @Test
    void shouldRejectNumericTypeWithTextValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureItemResult(
                                "ITEM-001",
                                "KOLB_CE",
                                ScientificFeatureItemResult
                                        .DataType.NUMERIC,
                                25.0,
                                "INVALID",
                                null,
                                null,
                                null
                        )
                )
                .withMessageContaining("NUMERIC");
    }

    @Test
    void shouldRejectTextTypeWithNumericValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureItemResult(
                                "ITEM-001",
                                "LEARNING_STYLE",
                                ScientificFeatureItemResult
                                        .DataType.TEXT,
                                25.0,
                                "DIVERGING",
                                null,
                                null,
                                null
                        )
                )
                .withMessageContaining("TEXT");
    }

    @Test
    void shouldRejectBooleanTypeWithTextValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() ->
                        new ScientificFeatureItemResult(
                                "ITEM-001",
                                "NEEDS_SUPPORT",
                                ScientificFeatureItemResult
                                        .DataType.BOOLEAN,
                                null,
                                "INVALID",
                                true,
                                null,
                                null
                        )
                )
                .withMessageContaining("BOOLEAN");
    }
}
