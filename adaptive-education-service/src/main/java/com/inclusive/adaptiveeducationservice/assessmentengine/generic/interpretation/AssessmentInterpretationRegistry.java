package com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class AssessmentInterpretationRegistry {

    private final List<AssessmentInterpretationStrategy> strategies;

    public AssessmentInterpretationRegistry(
            List<AssessmentInterpretationStrategy> strategies
    ) {
        this.strategies = strategies.stream()
                .sorted(
                        Comparator.comparingInt(
                                AssessmentInterpretationStrategy::priority
                        ).reversed()
                )
                .toList();
    }

    public AssessmentInterpretation interpret(
            AssessmentDefinition definition,
            AssessmentResult result
    ) {
        return strategies.stream()
                .filter(
                        strategy ->
                                strategy.supports(
                                        definition.code()
                                )
                )
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(
                                "No interpretation strategy supports: "
                                        + definition.code()
                        )
                )
                .interpret(definition, result);
    }
}