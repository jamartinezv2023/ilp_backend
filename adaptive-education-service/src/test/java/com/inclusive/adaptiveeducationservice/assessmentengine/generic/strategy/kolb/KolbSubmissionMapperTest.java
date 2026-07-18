package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KolbSubmissionMapperTest {

    private final KolbSubmissionMapper mapper =
            new KolbSubmissionMapper();

    @Test
    void shouldFlattenTwelveIpsativeQuestionsInStableOrder() {
        AssessmentDefinition definition = definition();
        AssessmentSubmission submission =
                submission(List.of(4, 3, 2, 1));

        List<Integer> answers =
                mapper.toAnswers(definition, submission);

        assertThat(answers).hasSize(48);

        assertThat(answers.subList(0, 4))
                .containsExactly(4, 3, 2, 1);

        assertThat(answers.subList(44, 48))
                .containsExactly(4, 3, 2, 1);
    }

    private AssessmentDefinition definition() {
        List<AssessmentQuestion> questions =
                new ArrayList<>();

        for (int questionNumber = 1;
             questionNumber <= 12;
             questionNumber++) {

            questions.add(
                    question(questionNumber)
            );
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

    private AssessmentQuestion question(
            int questionNumber
    ) {
        String prefix = "Q" + questionNumber;

        return new AssessmentQuestion(
                "KOLB-" + prefix,
                prefix,
                "Kolb question " + questionNumber,
                "CE_RO_AC_AE",
                AssessmentQuestionType.IPSATIVE_RANKING,
                true,
                questionNumber,
                List.of(
                        option(prefix, "CE", 1),
                        option(prefix, "RO", 2),
                        option(prefix, "AC", 3),
                        option(prefix, "AE", 4)
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

        for (int questionNumber = 1;
             questionNumber <= 12;
             questionNumber++) {

            String questionCode = "Q" + questionNumber;

            Map<String, Integer> rankings =
                    new LinkedHashMap<>();

            rankings.put(
                    questionCode + "-CE",
                    pattern.get(0)
            );

            rankings.put(
                    questionCode + "-RO",
                    pattern.get(1)
            );

            rankings.put(
                    questionCode + "-AC",
                    pattern.get(2)
            );

            rankings.put(
                    questionCode + "-AE",
                    pattern.get(3)
            );

            responses.add(
                    new AssessmentResponse(
                            questionCode,
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
                Map.of("source", "unit-test"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}