package com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentOption;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentQuestion;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentMetadataRegistry;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class AssessmentRendererModelFactory {

    private final AssessmentMetadataRegistry metadataRegistry;

    public AssessmentRendererModelFactory(
            AssessmentMetadataRegistry metadataRegistry
    ) {
        this.metadataRegistry = metadataRegistry;
    }

    public AssessmentRendererModel create(
            AssessmentDefinition definition
    ) {
        var questions = definition.questions()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                AssessmentQuestion::orderIndex
                        )
                )
                .map(this::toRendererQuestion)
                .toList();

        return new AssessmentRendererModel(
                definition.code(),
                definition.version(),
                definition.name(),
                definition.description(),
                definition.instructions(),
                metadataRegistry.metadataFor(definition),
                questions
        );
    }

    private AssessmentRendererQuestion toRendererQuestion(
            AssessmentQuestion question
    ) {
        var options = question.options()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                AssessmentOption::orderIndex
                        )
                )
                .map(this::toRendererOption)
                .toList();

        return new AssessmentRendererQuestion(
                question.id(),
                question.code(),
                question.text(),
                question.dimension(),
                question.type().name(),
                question.required(),
                question.orderIndex(),
                options
        );
    }

    private AssessmentRendererOption toRendererOption(
            AssessmentOption option
    ) {
        return new AssessmentRendererOption(
                option.id(),
                option.code(),
                option.text(),
                option.dimension(),
                option.numericValue(),
                option.weight(),
                option.orderIndex()
        );
    }
}