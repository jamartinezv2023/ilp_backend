package com.inclusive.adaptiveeducationservice.assessmentengine.generic.service;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentStrategyNotFoundException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentScoringStrategy;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.AssessmentStrategyRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssessmentStrategyRegistryTest {

    @Test
    void shouldReturnStrategySupportingAssessmentCode() {
        AssessmentScoringStrategy strategy =
                new SupportingStrategy("KOLB_V1");

        AssessmentStrategyRegistry registry =
                new AssessmentStrategyRegistry(List.of(strategy));

        AssessmentScoringStrategy selected =
                registry.findFor("KOLB_V1");

        assertEquals(strategy, selected);
        assertEquals(1, registry.registeredStrategyCount());
    }

    @Test
    void shouldRejectUnknownAssessmentCode() {
        AssessmentStrategyRegistry registry =
                new AssessmentStrategyRegistry(List.of());

        assertThrows(
                AssessmentStrategyNotFoundException.class,
                () -> registry.findFor("UNKNOWN")
        );
    }

    private record SupportingStrategy(
            String supportedCode
    ) implements AssessmentScoringStrategy {

        @Override
        public boolean supports(String assessmentCode) {
            return supportedCode.equals(assessmentCode);
        }

        @Override
        public AssessmentResult score(
                AssessmentDefinition definition,
                AssessmentSubmission submission
        ) {
            throw new UnsupportedOperationException();
        }
    }
}
