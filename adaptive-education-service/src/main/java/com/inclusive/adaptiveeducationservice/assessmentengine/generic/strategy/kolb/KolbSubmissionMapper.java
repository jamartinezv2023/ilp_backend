package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.InvalidAssessmentSubmissionException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KolbSubmissionMapper {

    private static final int EXPECTED_ANSWER_COUNT = 48;

    public List<Integer> toAnswers(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        Map<String, AssessmentResponse> responsesByQuestion =
                submission.responses()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        AssessmentResponse::questionCode,
                                        Function.identity(),
                                        (first, second) -> second
                                )
                        );

        List<Integer> answers = new ArrayList<>();

        definition.questions()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                AssessmentQuestion::orderIndex
                        )
                )
                .forEach(question -> appendQuestionAnswers(
                        question,
                        responsesByQuestion,
                        answers
                ));

        if (answers.size() != EXPECTED_ANSWER_COUNT) {
            throw new InvalidAssessmentSubmissionException(
                    "Kolb assessment requires exactly "
                            + EXPECTED_ANSWER_COUNT
                            + " ranked answers, but received "
                            + answers.size()
            );
        }

        return List.copyOf(answers);
    }

    private void appendQuestionAnswers(
            AssessmentQuestion question,
            Map<String, AssessmentResponse> responsesByQuestion,
            List<Integer> answers
    ) {
        AssessmentResponse response =
                responsesByQuestion.get(question.code());

        if (response == null) {
            throw new InvalidAssessmentSubmissionException(
                    "Missing Kolb response for question: "
                            + question.code()
            );
        }

        question.options()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                AssessmentOption::orderIndex
                        )
                )
                .map(option -> resolveRank(response, option))
                .forEach(answers::add);
    }

    private Integer resolveRank(
            AssessmentResponse response,
            AssessmentOption option
    ) {
        Integer rank = response.rankings().get(option.id());

        if (rank == null) {
            rank = response.rankings().get(option.code());
        }

        if (rank == null) {
            throw new InvalidAssessmentSubmissionException(
                    "Missing Kolb ranking for option: "
                            + option.id()
            );
        }

        return rank;
    }
}