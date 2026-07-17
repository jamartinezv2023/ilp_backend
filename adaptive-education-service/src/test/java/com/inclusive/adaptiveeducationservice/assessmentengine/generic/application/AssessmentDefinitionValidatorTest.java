package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AssessmentDefinitionValidatorTest {

    @Test
    void shouldRejectDuplicatedQuestionCodes() {
        AssessmentQuestion first =
                new AssessmentQuestion(
                        "Q-1",
                        "DUPLICATED",
                        "First question",
                        "TEST",
                        AssessmentQuestionType.SINGLE_CHOICE,
                        true,
                        1,
                        List.of()
                );

        AssessmentQuestion second =
                new AssessmentQuestion(
                        "Q-2",
                        "DUPLICATED",
                        "Second question",
                        "TEST",
                        AssessmentQuestionType.SINGLE_CHOICE,
                        true,
                        2,
                        List.of()
                );

        AssessmentDefinition definition =
                new AssessmentDefinition(
                        "DEF-1",
                        "TEST_V1",
                        "Test",
                        "Test definition",
                        "1.0",
                        "TEST_SCORING",
                        "TEST_INTERPRETATION",
                        "Instructions",
                        true,
                        List.of(first, second),
                        Instant.now(),
                        Instant.now()
                );

        AssessmentDefinitionValidator validator =
                new AssessmentDefinitionValidator();

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(definition)
        );
    }

    @Test
    void shouldRejectDuplicatedQuestionOrderIndexes() {
        AssessmentQuestion first =
                new AssessmentQuestion(
                        "Q-1",
                        "Q1",
                        "First question",
                        "TEST",
                        AssessmentQuestionType.SINGLE_CHOICE,
                        true,
                        1,
                        List.of()
                );

        AssessmentQuestion second =
                new AssessmentQuestion(
                        "Q-2",
                        "Q2",
                        "Second question",
                        "TEST",
                        AssessmentQuestionType.SINGLE_CHOICE,
                        true,
                        1,
                        List.of()
                );

        AssessmentDefinition definition =
                new AssessmentDefinition(
                        "DEF-1",
                        "TEST_V1",
                        "Test",
                        "Test definition",
                        "1.0",
                        "TEST_SCORING",
                        "TEST_INTERPRETATION",
                        "Instructions",
                        true,
                        List.of(first, second),
                        Instant.now(),
                        Instant.now()
                );

        AssessmentDefinitionValidator validator =
                new AssessmentDefinitionValidator();

        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(definition)
        );
    }
}