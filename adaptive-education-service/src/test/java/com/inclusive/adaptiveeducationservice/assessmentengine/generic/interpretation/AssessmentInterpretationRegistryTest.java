package com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentInterpretationRegistryTest {

    @Test
    void shouldUseHighestPriorityCompatibleStrategy() {
        AssessmentInterpretationStrategy fallback =
                strategy("FALLBACK", 0);

        AssessmentInterpretationStrategy specific =
                strategy("SPECIFIC", 100);

        AssessmentInterpretationRegistry registry =
                new AssessmentInterpretationRegistry(
                        List.of(fallback, specific)
                );

        var interpretation = registry.interpret(
                definition(),
                result()
        );

        assertThat(interpretation.primaryProfile())
                .isEqualTo("SPECIFIC");
    }

    private AssessmentInterpretationStrategy strategy(
            String profile,
            int priority
    ) {
        return new AssessmentInterpretationStrategy() {

            @Override
            public boolean supports(String assessmentCode) {
                return true;
            }

            @Override
            public AssessmentInterpretation interpret(
                    AssessmentDefinition definition,
                    AssessmentResult result
            ) {
                return new AssessmentInterpretation(
                        profile,
                        null,
                        profile,
                        Map.of(),
                        List.of()
                );
            }

            @Override
            public int priority() {
                return priority;
            }
        };
    }

    private AssessmentDefinition definition() {
        return new AssessmentDefinition(
                "DEF-TEST",
                "TEST_V1",
                "Test",
                "Test definition",
                "1.0",
                "TEST_SCORING",
                "TEST_INTERPRETATION",
                "Instructions",
                false,
                List.of(),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }

    private AssessmentResult result() {
        return new AssessmentResult(
                "ADMIN-1",
                "ST-1",
                "TEST_V1",
                "1.0",
                "PROFILE",
                Map.of("SCORE", 1.0),
                Map.of(),
                List.of(),
                "TEST_ALGORITHM_V1",
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}