package com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;

public interface AssessmentMetadataProvider {

    boolean supports(String assessmentCode);

    AssessmentMetadata provide(
            AssessmentDefinition definition
    );

    default int priority() {
        return 0;
    }
}