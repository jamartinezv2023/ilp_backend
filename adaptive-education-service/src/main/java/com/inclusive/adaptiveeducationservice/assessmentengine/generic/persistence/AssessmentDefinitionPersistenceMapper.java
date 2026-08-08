package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentDefinitionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentOptionEntity;
import com.inclusive.adaptiveeducationservice.assessmentdefinition.entity.AssessmentQuestionEntity;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestionType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class AssessmentDefinitionPersistenceMapper {

    private static final int DEFAULT_ESTIMATED_MINUTES = 20;
    private static final String DEFAULT_DESCRIPTION =
            "Assessment definition";
    private static final String DEFAULT_INSTRUCTIONS =
            "Complete every required question.";

    public AssessmentDefinition toDomain(
            AssessmentDefinitionEntity entity
    ) {
        List<AssessmentQuestion> questions =
                entity.getQuestions()
                        .stream()
                        .map(this::toDomainQuestion)
                        .toList();

        String strategy = valueOrDefault(
                entity.getAssessmentType(),
                "GENERIC"
        );

        Instant createdAt = entity.getCreatedAt() == null
                ? Instant.EPOCH
                : entity.getCreatedAt();

        return new AssessmentDefinition(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getVersion(),
                strategy,
                strategy + "_INTERPRETATION",
                entity.getInstructions(),
                Boolean.TRUE.equals(entity.getActive()),
                questions,
                createdAt,
                createdAt
        );
    }

    public AssessmentDefinitionEntity toEntity(
            AssessmentDefinition definition
    ) {
        String assessmentType = valueOrDefault(
                definition.scoringStrategy(),
                "GENERIC"
        );

        AssessmentDefinitionEntity entity =
                new AssessmentDefinitionEntity(
                        definition.id(),
                        definition.code(),
                        definition.name(),
                        valueOrDefault(
                                definition.description(),
                                DEFAULT_DESCRIPTION
                        ),
                        assessmentType,
                        definition.version(),
                        definition.active(),
                        DEFAULT_ESTIMATED_MINUTES,
                        valueOrDefault(
                                definition.instructions(),
                                DEFAULT_INSTRUCTIONS
                        ),
                        definition.createdAt() == null
                                ? Instant.now()
                                : definition.createdAt()
                );

        definition.questions()
                .stream()
                .map(this::toEntityQuestion)
                .forEach(entity::addQuestion);

        return entity;
    }

    private AssessmentQuestion toDomainQuestion(
            AssessmentQuestionEntity entity
    ) {
        AssessmentQuestionType type =
                parseQuestionType(entity.getQuestionType());

        List<AssessmentOption> options =
                entity.getOptions()
                        .stream()
                        .map(option -> toDomainOption(
                                option,
                                entity.getDimension()
                        ))
                        .toList();

        return new AssessmentQuestion(
                entity.getId(),
                "Q" + entity.getQuestionNumber(),
                entity.getText(),
                entity.getDimension(),
                type,
                Boolean.TRUE.equals(entity.getRequired()),
                entity.getDisplayOrder(),
                options
        );
    }

    private AssessmentQuestionEntity toEntityQuestion(
            AssessmentQuestion question
    ) {
        AssessmentQuestionEntity entity =
                new AssessmentQuestionEntity(
                        question.id(),
                        question.orderIndex(),
                        question.text(),
                        valueOrDefault(
                                question.dimension(),
                                "GENERAL"
                        ),
                        "",
                        question.required(),
                        question.type().name(),
                        question.orderIndex()
                );

        question.options()
                .stream()
                .map(this::toEntityOption)
                .forEach(entity::addOption);

        return entity;
    }

    private AssessmentOption toDomainOption(
            AssessmentOptionEntity entity,
            String questionDimension
    ) {
        double numericWeight = entity.getWeight() == null
                ? 0.0
                : entity.getWeight().doubleValue();

        return new AssessmentOption(
                entity.getId(),
                entity.getValue(),
                entity.getLabel(),
                questionDimension,
                numericWeight,
                numericWeight,
                entity.getDisplayOrder()
        );
    }

    private AssessmentOptionEntity toEntityOption(
            AssessmentOption option
    ) {
        return new AssessmentOptionEntity(
                option.id(),
                option.text(),
                option.code(),
                resolveIntegerWeight(option),
                option.orderIndex()
        );
    }

    private int resolveIntegerWeight(
            AssessmentOption option
    ) {
        Double source = option.weight() != null
                ? option.weight()
                : option.numericValue();

        if (source == null) {
            return 0;
        }

        return (int) Math.round(source);
    }

    private AssessmentQuestionType parseQuestionType(
            String questionType
    ) {
        if (
                questionType == null
                        || questionType.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Persisted question type is required"
            );
        }

        try {
            return AssessmentQuestionType.valueOf(
                    questionType
            );
        }
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unsupported persisted question type: "
                            + questionType,
                    exception
            );
        }
    }

    private String valueOrDefault(
            String value,
            String defaultValue
    ) {
        return value == null || value.isBlank()
                ? defaultValue
                : value;
    }
}