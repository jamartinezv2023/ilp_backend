package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;

public interface AssessmentScoringStrategy {

    boolean supports(String assessmentCode);

    AssessmentResult score(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    );
}
