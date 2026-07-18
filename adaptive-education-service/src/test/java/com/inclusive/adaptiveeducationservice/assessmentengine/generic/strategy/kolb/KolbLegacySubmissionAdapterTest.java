package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KolbLegacySubmissionAdapterTest {

    private final KolbLegacySubmissionAdapter adapter =
            new KolbLegacySubmissionAdapter();

    @Test
    void shouldConvertLegacyAnswersToGenericRankings() {
        List<Integer> answers = new ArrayList<>();

        for (int index = 0; index < 12; index++) {
            answers.addAll(List.of(4, 3, 2, 1));
        }

        var request =
                new KolbAssessmentRequest(
                        "ST-001",
                        answers
                );

        var submission =
                adapter.toGenericSubmission(
                        "ADMIN-001",
                        request,
                        definition()
                );

        assertThat(submission.participantId())
                .isEqualTo("ST-001");

        assertThat(submission.assessmentCode())
                .isEqualTo("KOLB_V1");

        assertThat(submission.responses())
                .hasSize(12);

        assertThat(
                submission.responses()
                        .get(0)
                        .rankings()
        ).containsEntry("Q1-CE", 4)
                .containsEntry("Q1-RO", 3)
                .containsEntry("Q1-AC", 2)
                .containsEntry("Q1-AE", 1);
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