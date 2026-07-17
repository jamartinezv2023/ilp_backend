package com.inclusive.adaptiveeducationservice.assessmentengine.generic.service;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.InvalidAssessmentSubmissionException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentScoringStrategy;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentStrategyRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenericAssessmentEngineTest {

    @Test
    void shouldValidateAndScoreValidSubmission() {
        AssessmentDefinition definition = definition();
        AssessmentSubmission submission = validSubmission();

        AssessmentScoringStrategy strategy =
                new TestScoringStrategy();

        GenericAssessmentEngine engine =
                new GenericAssessmentEngine(
                        new AssessmentStrategyRegistry(
                                List.of(strategy)
                        ),
                        new AssessmentSubmissionValidator()
                );

        AssessmentResult result =
                engine.evaluate(definition, submission);

        assertEquals("TEST_PROFILE", result.primaryProfile());
        assertEquals(1.0, result.scores().get("TOTAL"));
        assertEquals("TEST_V1", result.scoringAlgorithmVersion());
    }

    @Test
    void shouldRejectMissingRequiredResponse() {
        AssessmentDefinition definition = definition();

        AssessmentSubmission invalidSubmission =
                new AssessmentSubmission(
                        "ADMIN-001",
                        "ST-001",
                        "TEST_V1",
                        "1.0",
                        List.of(),
                        Map.of(),
                        Instant.now()
                );

        GenericAssessmentEngine engine =
                new GenericAssessmentEngine(
                        new AssessmentStrategyRegistry(
                                List.of(new TestScoringStrategy())
                        ),
                        new AssessmentSubmissionValidator()
                );

        assertThrows(
                InvalidAssessmentSubmissionException.class,
                () -> engine.evaluate(
                        definition,
                        invalidSubmission
                )
        );
    }

    private AssessmentDefinition definition() {
        AssessmentOption option =
                new AssessmentOption(
                        "OPTION-001",
                        "YES",
                        "Yes",
                        "TEST",
                        1.0,
                        1.0,
                        1
                );

        AssessmentQuestion question =
                new AssessmentQuestion(
                        "QUESTION-001",
                        "Q1",
                        "Test question",
                        "TEST",
                        AssessmentQuestionType.SINGLE_CHOICE,
                        true,
                        1,
                        List.of(option)
                );

        return new AssessmentDefinition(
                "DEFINITION-001",
                "TEST_V1",
                "Test assessment",
                "Generic engine test",
                "1.0",
                "TEST_STRATEGY",
                "TEST_INTERPRETATION",
                "Answer every question",
                true,
                List.of(question),
                Instant.now(),
                Instant.now()
        );
    }

    private AssessmentSubmission validSubmission() {
        AssessmentResponse response =
                new AssessmentResponse(
                        "Q1",
                        List.of("OPTION-001"),
                        Map.of(),
                        null,
                        null
                );

        return new AssessmentSubmission(
                "ADMIN-001",
                "ST-001",
                "TEST_V1",
                "1.0",
                List.of(response),
                Map.of("source", "unit-test"),
                Instant.now()
        );
    }

    private static class TestScoringStrategy
            implements AssessmentScoringStrategy {

        @Override
        public boolean supports(String assessmentCode) {
            return "TEST_V1".equals(assessmentCode);
        }

        @Override
        public AssessmentResult score(
                AssessmentDefinition definition,
                AssessmentSubmission submission
        ) {
            return new AssessmentResult(
                    submission.administrationId(),
                    submission.participantId(),
                    submission.assessmentCode(),
                    submission.assessmentVersion(),
                    "TEST_PROFILE",
                    Map.of("TOTAL", 1.0),
                    Map.of("TOTAL", "Test interpretation"),
                    List.of("Test recommendation"),
                    "TEST_V1",
                    Instant.now()
            );
        }
    }
}
