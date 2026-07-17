package com.inclusive.adaptiveeducationservice.assessmentengine.generic.service;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.InvalidAssessmentSubmissionException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AssessmentSubmissionValidator {

    public void validate(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        validateIdentity(definition, submission);
        validateRequiredQuestions(definition, submission);
        validateKnownQuestions(definition, submission);
    }

    private void validateIdentity(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        if (!definition.code().equals(submission.assessmentCode())) {
            throw new InvalidAssessmentSubmissionException(
                    "Submission assessment code does not match definition"
            );
        }

        if (!definition.version().equals(
                submission.assessmentVersion()
        )) {
            throw new InvalidAssessmentSubmissionException(
                    "Submission assessment version does not match definition"
            );
        }
    }

    private void validateRequiredQuestions(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        Map<String, AssessmentResponse> responseByQuestion =
                submission.responses().stream()
                        .collect(
                                Collectors.toMap(
                                        AssessmentResponse::questionCode,
                                        Function.identity(),
                                        (first, second) -> second
                                )
                        );

        for (AssessmentQuestion question : definition.questions()) {
            if (!question.required()) {
                continue;
            }

            AssessmentResponse response =
                    responseByQuestion.get(question.code());

            if (response == null || !response.hasValue()) {
                throw new InvalidAssessmentSubmissionException(
                        "Required question has no response: "
                                + question.code()
                );
            }
        }
    }

    private void validateKnownQuestions(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        Set<String> knownQuestionCodes =
                definition.questions().stream()
                        .map(AssessmentQuestion::code)
                        .collect(Collectors.toSet());

        for (AssessmentResponse response : submission.responses()) {
            if (!knownQuestionCodes.contains(response.questionCode())) {
                throw new InvalidAssessmentSubmissionException(
                        "Unknown question code: "
                                + response.questionCode()
                );
            }
        }
    }
}
