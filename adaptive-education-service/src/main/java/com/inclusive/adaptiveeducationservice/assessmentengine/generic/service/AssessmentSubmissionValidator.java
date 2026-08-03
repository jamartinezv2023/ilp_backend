package com.inclusive.adaptiveeducationservice.assessmentengine.generic.service;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.InvalidAssessmentSubmissionException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class AssessmentSubmissionValidator {

    public void validate(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        validateIdentity(definition, submission);

        Map<String, AssessmentQuestion> questionsByCode =
                definition.questions()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        AssessmentQuestion::code,
                                        question -> question
                                )
                        );

        Map<String, AssessmentResponse> responsesByQuestion =
                indexResponses(submission);

        validateUnknownQuestions(
                questionsByCode,
                responsesByQuestion
        );

        for (AssessmentQuestion question :
                definition.questions()) {

            AssessmentResponse response =
                    responsesByQuestion.get(question.code());

            if (question.required()
                    && (response == null || !response.hasValue())) {
                throw invalid(
                        "Required response is missing for question: "
                                + question.code()
                );
            }

            if (response != null
                    && question.type()
                    == AssessmentQuestionType.IPSATIVE_RANKING) {
                validateIpsativeRanking(
                        question,
                        response
                );
            }
        }
    }

    private void validateIdentity(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        if (!definition.code().equals(
                submission.assessmentCode()
        )) {
            throw invalid(
                    "Assessment code does not match definition"
            );
        }

        if (!definition.version().equals(
                submission.assessmentVersion()
        )) {
            throw invalid(
                    "Assessment version does not match definition"
            );
        }
    }

    private Map<String, AssessmentResponse> indexResponses(
            AssessmentSubmission submission
    ) {
        Map<String, AssessmentResponse> indexed =
                new HashMap<>();

        for (AssessmentResponse response :
                submission.responses()) {

            AssessmentResponse previous =
                    indexed.put(
                            response.questionCode(),
                            response
                    );

            if (previous != null) {
                throw invalid(
                        "Duplicate response for question: "
                                + response.questionCode()
                );
            }
        }

        return indexed;
    }

    private void validateUnknownQuestions(
            Map<String, AssessmentQuestion> questionsByCode,
            Map<String, AssessmentResponse> responsesByQuestion
    ) {
        for (String responseCode :
                responsesByQuestion.keySet()) {

            if (!questionsByCode.containsKey(responseCode)) {
                throw invalid(
                        "Unknown question code: "
                                + responseCode
                );
            }
        }
    }

    private void validateIpsativeRanking(
            AssessmentQuestion question,
            AssessmentResponse response
    ) {
        Map<String, Integer> rankings =
                response.rankings();

        int optionCount = question.options().size();

        if (rankings.size() != optionCount) {
            throw invalid(
                    "Ipsative question "
                            + question.code()
                            + " requires exactly "
                            + optionCount
                            + " rankings"
            );
        }

        Set<String> normalizedOptionIds =
                new HashSet<>();

        for (Map.Entry<String, Integer> ranking :
                rankings.entrySet()) {

            AssessmentOption option =
                    resolveOption(
                            question,
                            ranking.getKey()
                    );

            if (!normalizedOptionIds.add(option.id())) {
                throw invalid(
                        "Repeated option in ipsative question: "
                                + question.code()
                );
            }

            if (ranking.getValue() == null) {
                throw invalid(
                        "Null ranking in question: "
                                + question.code()
                );
            }
        }

        Set<Integer> actualValues =
                new HashSet<>(rankings.values());

        Set<Integer> expectedValues =
                IntStream
                        .rangeClosed(1, optionCount)
                        .boxed()
                        .collect(Collectors.toSet());

        if (!actualValues.equals(expectedValues)) {
            throw invalid(
                    "Ipsative question "
                            + question.code()
                            + " must use each ranking from 1 to "
                            + optionCount
                            + " exactly once"
            );
        }

        if (!response.selectedOptionIds().isEmpty()
                || response.numericValue() != null
                || (response.textValue() != null
                && !response.textValue().isBlank())) {
            throw invalid(
                    "Ipsative question "
                            + question.code()
                            + " must contain rankings only"
            );
        }
    }

    private AssessmentOption resolveOption(
            AssessmentQuestion question,
            String suppliedOptionKey
    ) {
        return question.options()
                .stream()
                .filter(option ->
                        option.id().equals(suppliedOptionKey)
                                || option.code().equals(
                                suppliedOptionKey
                        )
                )
                .findFirst()
                .orElseThrow(() ->
                        invalid(
                                "Unknown option "
                                        + suppliedOptionKey
                                        + " for question "
                                        + question.code()
                        )
                );
    }

    private InvalidAssessmentSubmissionException invalid(
            String message
    ) {
        return new InvalidAssessmentSubmissionException(
                message
        );
    }
}