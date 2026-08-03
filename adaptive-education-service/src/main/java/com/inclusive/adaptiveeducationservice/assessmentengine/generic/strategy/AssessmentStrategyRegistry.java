package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentStrategyNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AssessmentStrategyRegistry {

    private final List<AssessmentScoringStrategy> strategies;

    public AssessmentStrategyRegistry(
            List<AssessmentScoringStrategy> strategies
    ) {
        this.strategies = List.copyOf(strategies);
    }

    public AssessmentScoringStrategy findFor(String assessmentCode) {
        Objects.requireNonNull(
                assessmentCode,
                "Assessment code is required"
        );

        return strategies.stream()
                .filter(strategy -> strategy.supports(assessmentCode))
                .findFirst()
                .orElseThrow(
                        () -> new AssessmentStrategyNotFoundException(
                                assessmentCode
                        )
                );
    }

    public int registeredStrategyCount() {
        return strategies.size();
    }
}
