package com.inclusive.adaptiveeducationservice.assessmentengine.generic.service;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentScoringStrategy;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentStrategyRegistry;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GenericAssessmentEngine {

    private final AssessmentStrategyRegistry strategyRegistry;
    private final AssessmentSubmissionValidator submissionValidator;

    public GenericAssessmentEngine(
            AssessmentStrategyRegistry strategyRegistry,
            AssessmentSubmissionValidator submissionValidator
    ) {
        this.strategyRegistry = strategyRegistry;
        this.submissionValidator = submissionValidator;
    }

    public AssessmentResult evaluate(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        Objects.requireNonNull(
                definition,
                "Assessment definition is required"
        );
        Objects.requireNonNull(
                submission,
                "Assessment submission is required"
        );

        submissionValidator.validate(definition, submission);

        AssessmentScoringStrategy strategy =
                strategyRegistry.findFor(definition.code());

        return strategy.score(definition, submission);
    }
}
