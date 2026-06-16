package com.inclusive.adaptiveeducationservice.assessmentdefinition.service;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.dto.AssessmentDefinitionResponse;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.dto.AssessmentOptionResponse;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.dto.AssessmentQuestionResponse;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentOptionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentQuestionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AssessmentDefinitionService {

    private final AssessmentDefinitionRepository repository;

    public AssessmentDefinitionService(
            AssessmentDefinitionRepository repository
    ) {
        this.repository = repository;
    }

    public List<AssessmentDefinitionResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AssessmentDefinitionResponse findActiveByCode(String code) {
        return repository.findByCodeAndActiveTrue(code)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Assessment definition not found"
                ));
    }

    private AssessmentDefinitionResponse toResponse(
            AssessmentDefinitionEntity definition
    ) {
        return new AssessmentDefinitionResponse(
                definition.getId(),
                definition.getCode(),
                definition.getName(),
                definition.getDescription(),
                definition.getAssessmentType(),
                definition.getVersion(),
                definition.getActive(),
                definition.getEstimatedMinutes(),
                definition.getInstructions(),
                definition.getCreatedAt(),
                definition.getQuestions()
                        .stream()
                        .map(this::toQuestionResponse)
                        .toList()
        );
    }

    private AssessmentQuestionResponse toQuestionResponse(
            AssessmentQuestionEntity question
    ) {
        return new AssessmentQuestionResponse(
                question.getId(),
                question.getQuestionNumber(),
                question.getText(),
                question.getDimension(),
                question.getHelpText(),
                question.getRequired(),
                question.getQuestionType(),
                question.getDisplayOrder(),
                question.getOptions()
                        .stream()
                        .map(this::toOptionResponse)
                        .toList()
        );
    }

    private AssessmentOptionResponse toOptionResponse(
            AssessmentOptionEntity option
    ) {
        return new AssessmentOptionResponse(
                option.getId(),
                option.getLabel(),
                option.getValue(),
                option.getWeight(),
                option.getDisplayOrder()
        );
    }
}