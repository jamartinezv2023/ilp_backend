package com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb;

import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentDefinitionNotFoundException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.AssessmentDefinitionRepositoryPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.service.GenericAssessmentEngine;
import org.springframework.stereotype.Component;

@Component
public class KolbGenericAssessmentFacade {

    private static final String ASSESSMENT_CODE = "KOLB_V1";

    private final GenericAssessmentEngine genericAssessmentEngine;
    private final AssessmentDefinitionRepositoryPort definitionRepository;
    private final KolbLegacySubmissionAdapter submissionAdapter;

    public KolbGenericAssessmentFacade(
            GenericAssessmentEngine genericAssessmentEngine,
            AssessmentDefinitionRepositoryPort definitionRepository,
            KolbLegacySubmissionAdapter submissionAdapter
    ) {
        this.genericAssessmentEngine = genericAssessmentEngine;
        this.definitionRepository = definitionRepository;
        this.submissionAdapter = submissionAdapter;
    }

    public AssessmentResult evaluate(
            String administrationId,
            KolbAssessmentRequest request
    ) {
        var definition =
                definitionRepository
                        .findLatestActiveByCode(ASSESSMENT_CODE)
                        .orElseThrow(
                                () ->
                                        new AssessmentDefinitionNotFoundException(
                                                ASSESSMENT_CODE
                                        )
                        );

        var submission =
                submissionAdapter.toGenericSubmission(
                        administrationId,
                        request,
                        definition
                );

        return genericAssessmentEngine.evaluate(
                definition,
                submission
        );
    }
}