package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.InvalidAssessmentSubmissionException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class KolbLegacySubmissionAdapter {

    public AssessmentSubmission toGenericSubmission(
            String administrationId,
            KolbAssessmentRequest request,
            AssessmentDefinition definition
    ) {
        List<AssessmentQuestion> orderedQuestions =
                definition.questions()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        AssessmentQuestion::orderIndex
                                )
                        )
                        .toList();

        int expectedAnswerCount = orderedQuestions.stream()
                .mapToInt(question -> question.options().size())
                .sum();

        if (request.answers().size() != expectedAnswerCount) {
            throw new InvalidAssessmentSubmissionException(
                    "Kolb definition requires "
                            + expectedAnswerCount
                            + " answers, but received "
                            + request.answers().size()
            );
        }

        List<AssessmentResponse> responses =
                new ArrayList<>();

        int answerIndex = 0;

        for (AssessmentQuestion question : orderedQuestions) {
            List<AssessmentOption> orderedOptions =
                    question.options()
                            .stream()
                            .sorted(
                                    Comparator.comparingInt(
                                            AssessmentOption::orderIndex
                                    )
                            )
                            .toList();

            Map<String, Integer> rankings =
                    new LinkedHashMap<>();

            for (AssessmentOption option : orderedOptions) {
                rankings.put(
                        option.id(),
                        request.answers().get(answerIndex)
                );

                answerIndex++;
            }

            responses.add(
                    new AssessmentResponse(
                            question.code(),
                            List.of(),
                            rankings,
                            null,
                            null
                    )
            );
        }

        return new AssessmentSubmission(
                administrationId,
                request.studentId(),
                definition.code(),
                definition.version(),
                responses,
                Map.of(
                        "source",
                        "legacy-kolb-rest-endpoint"
                ),
                Instant.now()
        );
    }
}