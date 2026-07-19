package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentDefinitionNotFoundException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.AssessmentDefinitionRepositoryPort;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering.AssessmentRendererModel;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering.AssessmentRendererModelFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenericAssessmentDefinitionQueryService {

    private final AssessmentDefinitionRepositoryPort definitionRepository;

    private final AssessmentRendererModelFactory
            rendererModelFactory;

    public GenericAssessmentDefinitionQueryService(
            AssessmentDefinitionRepositoryPort definitionRepository,
            AssessmentRendererModelFactory rendererModelFactory
    ) {
        this.definitionRepository = definitionRepository;
        this.rendererModelFactory = rendererModelFactory;
    }

    public AssessmentRendererModel findRendererModel(
            String assessmentCode
    ) {
        requireCode(assessmentCode);

        var definition =
                definitionRepository
                        .findLatestActiveByCode(assessmentCode)
                        .orElseThrow(
                                () ->
                                        new AssessmentDefinitionNotFoundException(
                                                assessmentCode
                                        )
                        );

        return rendererModelFactory.create(definition);
    }

    public List<AssessmentRendererModel> findAllActiveRendererModels() {
        return definitionRepository
                .findAllActive()
                .stream()
                .map(rendererModelFactory::create)
                .toList();
    }

    private void requireCode(String assessmentCode) {
        if (
                assessmentCode == null
                || assessmentCode.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Assessment code is required"
            );
        }
    }
}