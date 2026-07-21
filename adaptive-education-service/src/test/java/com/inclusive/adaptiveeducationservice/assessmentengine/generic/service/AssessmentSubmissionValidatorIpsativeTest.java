package com.inclusive.adaptiveeducationservice.assessmentengine.generic.service;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.InvalidAssessmentSubmissionException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentSubmissionValidatorIpsativeTest {

    private final AssessmentSubmissionValidator validator =
            new AssessmentSubmissionValidator();

    @Test
    void shouldAcceptCompleteUniqueIpsativeRanking() {
        assertThatCode(() ->
                validator.validate(
                        definition(),
                        submission(
                                Map.of(
                                        "Q1-CE", 4,
                                        "Q1-RO", 3,
                                        "Q1-AC", 2,
                                        "Q1-AE", 1
                                )
                        )
                )
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectDuplicatedIpsativeValues() {
        assertThatThrownBy(() ->
                validator.validate(
                        definition(),
                        submission(
                                Map.of(
                                        "Q1-CE", 4,
                                        "Q1-RO", 4,
                                        "Q1-AC", 4,
                                        "Q1-AE", 4
                                )
                        )
                )
        )
                .isInstanceOf(
                        InvalidAssessmentSubmissionException.class
                )
                .hasMessageContaining(
                        "must use each ranking"
                );
    }

    @Test
    void shouldRejectMissingIpsativeOption() {
        assertThatThrownBy(() ->
                validator.validate(
                        definition(),
                        submission(
                                Map.of(
                                        "Q1-CE", 4,
                                        "Q1-RO", 3,
                                        "Q1-AC", 2
                                )
                        )
                )
        )
                .isInstanceOf(
                        InvalidAssessmentSubmissionException.class
                )
                .hasMessageContaining(
                        "requires exactly 4 rankings"
                );
    }

    @Test
    void shouldRejectUnknownIpsativeOption() {
        assertThatThrownBy(() ->
                validator.validate(
                        definition(),
                        submission(
                                Map.of(
                                        "Q1-CE", 4,
                                        "Q1-RO", 3,
                                        "Q1-AC", 2,
                                        "Q1-UNKNOWN", 1
                                )
                        )
                )
        )
                .isInstanceOf(
                        InvalidAssessmentSubmissionException.class
                )
                .hasMessageContaining(
                        "Unknown option"
                );
    }

    private AssessmentDefinition definition() {
        AssessmentQuestion question =
                new AssessmentQuestion(
                        "KOLB-Q1",
                        "Q1",
                        "Question 1",
                        "CE_RO_AC_AE",
                        AssessmentQuestionType.IPSATIVE_RANKING,
                        true,
                        1,
                        List.of(
                                option("CE", 1),
                                option("RO", 2),
                                option("AC", 3),
                                option("AE", 4)
                        )
                );

        return new AssessmentDefinition(
                "DEF-KOLB-V1",
                "KOLB_V1",
                "Kolb",
                "Kolb test",
                "1.0",
                "KOLB_BASELINE_V1",
                "KOLB_INTERPRETATION_V1",
                "Rank every option",
                true,
                List.of(question),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }

    private AssessmentOption option(
            String code,
            int order
    ) {
        return new AssessmentOption(
                "Q1-" + code,
                code,
                code,
                code,
                null,
                null,
                order
        );
    }

    private AssessmentSubmission submission(
            Map<String, Integer> rankings
    ) {
        return new AssessmentSubmission(
                "ADMIN-001",
                "ST-001",
                "KOLB_V1",
                "1.0",
                List.of(
                        new AssessmentResponse(
                                "Q1",
                                List.of(),
                                rankings,
                                null,
                                null
                        )
                ),
                Map.of("source", "test"),
                Instant.parse("2026-01-01T00:00:00Z")
        );
    }
}