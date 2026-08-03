package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentScientificContextReaderTest {

    @Test
    void shouldReadStringScientificContext() {
        AssessmentScientificContextReader reader =
                new AssessmentScientificContextReader(
                        Map.of(
                                "institutionId",
                                "INST-001",
                                "durationSeconds",
                                "600",
                                "featureCutoffAt",
                                "2026-07-22T10:00:00Z"
                        )
                );

        assertThat(reader.text("institutionId"))
                .isEqualTo("INST-001");

        assertThat(reader.longValue("durationSeconds"))
                .isEqualTo(600L);

        assertThat(reader.instant("featureCutoffAt"))
                .isEqualTo(
                        Instant.parse(
                                "2026-07-22T10:00:00Z"
                        )
                );
    }

    @Test
    void shouldAcceptGenericObjectValues() {
        AssessmentScientificContextReader reader =
                new AssessmentScientificContextReader(
                        Map.of(
                                "durationSeconds",
                                600L,
                                "featureCutoffAt",
                                Instant.parse(
                                        "2026-07-22T10:00:00Z"
                                )
                        )
                );

        assertThat(reader.longValue("durationSeconds"))
                .isEqualTo(600L);

        assertThat(reader.instant("featureCutoffAt"))
                .isEqualTo(
                        Instant.parse(
                                "2026-07-22T10:00:00Z"
                        )
                );
    }

    @Test
    void shouldReturnNullForMissingValues() {
        AssessmentScientificContextReader reader =
                new AssessmentScientificContextReader(
                        Map.of()
                );

        assertThat(reader.text("missing"))
                .isNull();

        assertThat(reader.longValue("missing"))
                .isNull();

        assertThat(reader.instant("missing"))
                .isNull();
    }

    @Test
    void shouldCreateIndependentCompleteContext() {
        AssessmentScientificContextReader reader =
                new AssessmentScientificContextReader(
                        Map.of(
                                "institutionId",
                                "INST-001",
                                "academicYear",
                                "2026"
                        )
                );

        assertThat(reader.completeContext())
                .containsEntry(
                        "institutionId",
                        "INST-001"
                )
                .containsEntry(
                        "academicYear",
                        "2026"
                );
    }

    @Test
    void shouldRejectInvalidDuration() {
        AssessmentScientificContextReader reader =
                new AssessmentScientificContextReader(
                        Map.of(
                                "durationSeconds",
                                "not-a-number"
                        )
                );

        assertThatThrownBy(() ->
                reader.longValue(
                        "durationSeconds"
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "must be numeric"
                );
    }

    @Test
    void shouldRejectInvalidInstant() {
        AssessmentScientificContextReader reader =
                new AssessmentScientificContextReader(
                        Map.of(
                                "featureCutoffAt",
                                "invalid-date"
                        )
                );

        assertThatThrownBy(() ->
                reader.instant(
                        "featureCutoffAt"
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessageContaining(
                        "ISO-8601"
                );
    }
}