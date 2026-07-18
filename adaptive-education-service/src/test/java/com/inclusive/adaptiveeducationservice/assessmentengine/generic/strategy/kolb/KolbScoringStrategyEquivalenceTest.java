package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentStrategyRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KolbScoringStrategyEquivalenceTest {

    private final KolbAssessmentEngine legacyEngine =
            new KolbAssessmentEngine();

    private final KolbScoringStrategy strategy =
            new KolbScoringStrategy(
                    legacyEngine,
                    new KolbSubmissionMapper(),
                    new KolbResultMapper()
            );

    @Test
    void shouldMatchLegacyDivergentResult() {
        assertEquivalent(
                List.of(4, 4, 1, 1),
                "DIVERGENT"
        );
    }

    @Test
    void shouldMatchLegacyAssimilatingResult() {
        assertEquivalent(
                List.of(1, 4, 4, 1),
                "ASSIMILATING"
        );
    }

    @Test
    void shouldMatchLegacyConvergentResult() {
        assertEquivalent(
                List.of(1, 1, 4, 4),
                "CONVERGENT"
        );
    }

    @Test
    void shouldMatchLegacyAccommodatingResult() {
        assertEquivalent(
                List.of(4, 1, 1, 4),
                "ACCOMMODATING"
        );
    }

    @Test
    void shouldBeDiscoverableByStrategyRegistry() {
        AssessmentStrategyRegistry registry =
                new AssessmentStrategyRegistry(
                        List.of(strategy)
                );

        assertThat(registry.findFor("KOLB_V1"))
                .isSameAs(strategy);
    }

    private void assertEquivalent(
            List<Integer> pattern,
            String expectedStyle
    ) {
        List<Integer> legacyAnswers =
                repeatPattern(pattern);

        var legacyResult =
                legacyEngine.calculate(legacyAnswers);

        AssessmentResult genericResult =
                strategy.score(
                        definition(),
                        submission(pattern)
                );

        assertThat(genericResult.primaryProfile())
                .isEqualTo(legacyResult.learningStyle())
                .isEqualTo(expectedStyle);

        assertThat(genericResult.scores().get("CE"))
                .isEqualTo(
                        legacyResult.scoreCE().doubleValue()
                );

        assertThat(genericResult.scores().get("RO"))
                .isEqualTo(
                        legacyResult.scoreRO().doubleValue()
                );

        assertThat(genericResult.scores().get("AC"))
                .isEqualTo(
                        legacyResult.scoreAC().doubleValue()
                );

        assertThat(genericResult.scores().get("AE"))
                .isEqualTo(
                        legacyResult.scoreAE().doubleValue()
                );

        assertThat(
                genericResult.scores().get("AC_MINUS_CE")
        ).isEqualTo(
                (double) (
                        legacyResult.scoreAC()
                                - legacyResult.scoreCE()
                )
        );

        assertThat(
                genericResult.scores().get("AE_MINUS_RO")
        ).isEqualTo(
                (double) (
                        legacyResult.scoreAE()
                                - legacyResult.scoreRO()
                )
        );

        assertThat(
                genericResult.scoringAlgorithmVersion()
        ).isEqualTo("KOLB_BASELINE_V1");
    }

    private List<Integer> repeatPattern(
            List<Integer> pattern
    ) {
        List<Integer> answers = new ArrayList<>();

        for (int index = 0; index < 12; index++) {
            answers.addAll(pattern);
        }

        return answers;
    }

    private AssessmentDefinition definition() {
        List<AssessmentQuestion> questions =
                new ArrayList<>();

        for (int number = 1; number <= 12; number++) {
            questions.add(question(number));
        }

        return new AssessmentDefinition(
                "DEF-KOLB-V1",
                "KOLB_V1",
                "Kolb Learning Style Inventory",
                "Kolb test",
                "1.0",
                "KOLB_BASELINE_V1",
                "KOLB_INTERPRETATION_V1",
                "Rank every option.",
                true,
                questions,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }

    private AssessmentQuestion question(int number) {
        String code = "Q" + number;

        return new AssessmentQuestion(
                "KOLB-" + code,
                code,
                "Kolb question " + number,
                "CE_RO_AC_AE",
                AssessmentQuestionType.IPSATIVE_RANKING,
                true,
                number,
                List.of(
                        option(code, "CE", 1),
                        option(code, "RO", 2),
                        option(code, "AC", 3),
                        option(code, "AE", 4)
                )
        );
    }

    private AssessmentOption option(
            String questionCode,
            String dimension,
            int order
    ) {
        return new AssessmentOption(
                questionCode + "-" + dimension,
                dimension,
                dimension,
                dimension,
                null,
                null,
                order
        );
    }

    private AssessmentSubmission submission(
            List<Integer> pattern
    ) {
        List<AssessmentResponse> responses =
                new ArrayList<>();

        for (int number = 1; number <= 12; number++) {
            String code = "Q" + number;

            Map<String, Integer> rankings =
                    new LinkedHashMap<>();

            rankings.put(code + "-CE", pattern.get(0));
            rankings.put(code + "-RO", pattern.get(1));
            rankings.put(code + "-AC", pattern.get(2));
            rankings.put(code + "-AE", pattern.get(3));

            responses.add(
                    new AssessmentResponse(
                            code,
                            List.of(),
                            rankings,
                            null,
                            null
                    )
            );
        }

        return new AssessmentSubmission(
                "ADMIN-KOLB-001",
                "ST-001",
                "KOLB_V1",
                "1.0",
                responses,
                Map.of("source", "equivalence-test"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}