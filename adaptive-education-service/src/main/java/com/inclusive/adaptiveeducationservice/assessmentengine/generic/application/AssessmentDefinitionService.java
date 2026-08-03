package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.AssessmentDefinitionNotFoundException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception.DuplicateAssessmentDefinitionException;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.AssessmentDefinitionRepositoryPort;

import java.util.List;
import java.util.Objects;

public class AssessmentDefinitionService {

    private final AssessmentDefinitionRepositoryPort repository;
    private final AssessmentDefinitionValidator validator;

    public AssessmentDefinitionService(
            AssessmentDefinitionRepositoryPort repository,
            AssessmentDefinitionValidator validator
    ) {
        this.repository = repository;
        this.validator = validator;
    }

    public AssessmentDefinition register(
            AssessmentDefinition definition
    ) {
        Objects.requireNonNull(
                definition,
                "Assessment definition is required"
        );

        validator.validate(definition);

        if (
                repository.existsByCodeAndVersion(
                        definition.code(),
                        definition.version()
                )
        ) {
            throw new DuplicateAssessmentDefinitionException(
                    definition.code(),
                    definition.version()
            );
        }

        return repository.save(definition);
    }

    public AssessmentDefinition find(
            String assessmentCode,
            String version
    ) {
        requireText(assessmentCode, "Assessment code");
        requireText(version, "Assessment version");

        return repository.findByCodeAndVersion(
                        assessmentCode,
                        version
                )
                .orElseThrow(
                        () -> new AssessmentDefinitionNotFoundException(
                                assessmentCode,
                                version
                        )
                );
    }

    public AssessmentDefinition findLatestActive(
            String assessmentCode
    ) {
        requireText(assessmentCode, "Assessment code");

        return repository.findLatestActiveByCode(assessmentCode)
                .orElseThrow(
                        () -> new AssessmentDefinitionNotFoundException(
                                assessmentCode
                        )
                );
    }

    public List<AssessmentDefinition> findVersions(
            String assessmentCode
    ) {
        requireText(assessmentCode, "Assessment code");

        return List.copyOf(
                repository.findAllByCode(assessmentCode)
        );
    }

    public List<AssessmentDefinition> findAllActive() {
        return List.copyOf(repository.findAllActive());
    }

    private void requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }
    }
}