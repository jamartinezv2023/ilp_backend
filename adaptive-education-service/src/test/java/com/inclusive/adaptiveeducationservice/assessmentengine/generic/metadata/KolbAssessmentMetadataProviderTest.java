package com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KolbAssessmentMetadataProviderTest {

    @Test
    void shouldDescribeKolbAsLearningStyleInstrument() {
        var provider =
                new KolbAssessmentMetadataProvider();

        var metadata =
                provider.provide(definition());

        assertThat(metadata.code())
                .isEqualTo("KOLB_V1");

        assertThat(metadata.author())
                .isEqualTo("David A. Kolb");

        assertThat(metadata.instrumentType())
                .isEqualTo(
                        AssessmentInstrumentType.LEARNING_STYLE
                );

        assertThat(metadata.language())
                .isEqualTo("es");
    }

    @Test
    void shouldHavePriorityOverDefaultProvider() {
        var registry =
                new AssessmentMetadataRegistry(
                        List.of(
                                new DefaultAssessmentMetadataProvider(),
                                new KolbAssessmentMetadataProvider()
                        )
                );

        var metadata =
                registry.metadataFor(definition());

        assertThat(metadata.instrumentType())
                .isEqualTo(
                        AssessmentInstrumentType.LEARNING_STYLE
                );
    }

    private AssessmentDefinition definition() {
        return new AssessmentDefinition(
                "DEF-KOLB-V1",
                "KOLB_V1",
                "Kolb Learning Style Inventory",
                "Learning style inventory",
                "1.0",
                "KOLB_BASELINE_V1",
                "KOLB_INTERPRETATION_V1",
                "Rank every option.",
                true,
                List.of(),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}