package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.service.KolbAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.AssessmentDefinitionRepositoryPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.AssessmentSubmissionValidator;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentStrategyRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KolbGenericAssessmentFacadeTest {

    @Test
    void shouldEvaluateLegacyRequestThroughGenericEngine() {
        AssessmentDefinition definition = definition();

        AssessmentDefinitionRepositoryPort repository =
                mock(AssessmentDefinitionRepositoryPort.class);

        when(
                repository.findLatestActiveByCode("KOLB_V1")
        ).thenReturn(Optional.of(definition));

        KolbScoringStrategy strategy =
                new KolbScoringStrategy(
                        new KolbAssessmentEngine(),
                        new KolbSubmissionMapper(),
                        new KolbResultMapper()
                );

        GenericAssessmentEngine engine =
                new GenericAssessmentEngine(
                        new AssessmentStrategyRegistry(
                                List.of(strategy)
                        ),
                        new AssessmentSubmissionValidator()
                );

        KolbGenericAssessmentFacade facade =
                new KolbGenericAssessmentFacade(
                        engine,
                        repository,
                        new KolbLegacySubmissionAdapter()
                );

        List<Integer> answers = new ArrayList<>();

        for (int index = 0; index < 12; index++) {
            answers.addAll(List.of(4, 3, 2, 1));
        }

        var result =
                facade.evaluate(
                        "ADMIN-001",
                        new KolbAssessmentRequest(
                                "ST-001",
                                answers
                        )
                );

        assertThat(result.primaryProfile())
                .isEqualTo("DIVERGENT");

        assertThat(result.scores().get("CE"))
                .isEqualTo(48.0);

        assertThat(result.scores().get("RO"))
                .isEqualTo(36.0);

        assertThat(result.scores().get("AC"))
                .isEqualTo(24.0);

        assertThat(result.scores().get("AE"))
                .isEqualTo(12.0);

        assertThat(result.scores().get("AC_MINUS_CE"))
                .isEqualTo(-24.0);

        assertThat(result.scores().get("AE_MINUS_RO"))
                .isEqualTo(-24.0);

        assertThat(result.scoringAlgorithmVersion())
                .isEqualTo("KOLB_BASELINE_V1");
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
                "Kolb assessment",
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
                "Question " + number,
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
}