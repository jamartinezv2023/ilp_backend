package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation.AssessmentInterpretation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentMetadata;

import java.time.Instant;

public record InterpretedAssessmentResult(

        AssessmentResult rawResult,

        AssessmentMetadata metadata,

        AssessmentInterpretation interpretation,

        Instant interpretedAt
) {
}