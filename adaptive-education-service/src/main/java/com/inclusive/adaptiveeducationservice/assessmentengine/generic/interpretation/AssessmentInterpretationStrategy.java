package com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;

public interface AssessmentInterpretationStrategy {

    boolean supports(String assessmentCode);

    AssessmentInterpretation interpret(
            AssessmentDefinition definition,
            AssessmentResult result
    );

    default int priority() {
        return 0;
    }
}