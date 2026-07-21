package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation.AssessmentInterpretationRegistry;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentMetadataRegistry;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InstrumentIndependentAssessmentService {

    private final GenericAssessmentEngine assessmentEngine;

    private final AssessmentMetadataRegistry metadataRegistry;

    private final AssessmentInterpretationRegistry
            interpretationRegistry;

    public InstrumentIndependentAssessmentService(
            GenericAssessmentEngine assessmentEngine,
            AssessmentMetadataRegistry metadataRegistry,
            AssessmentInterpretationRegistry interpretationRegistry
    ) {
        this.assessmentEngine = assessmentEngine;
        this.metadataRegistry = metadataRegistry;
        this.interpretationRegistry = interpretationRegistry;
    }

    public InterpretedAssessmentResult evaluate(
            AssessmentDefinition definition,
            AssessmentSubmission submission
    ) {
        var rawResult =
                assessmentEngine.evaluate(
                        definition,
                        submission
                );

        var metadata =
                metadataRegistry.metadataFor(definition);

        var interpretation =
                interpretationRegistry.interpret(
                        definition,
                        rawResult
                );

        return new InterpretedAssessmentResult(
                rawResult,
                metadata,
                interpretation,
                Instant.now()
        );
    }
}