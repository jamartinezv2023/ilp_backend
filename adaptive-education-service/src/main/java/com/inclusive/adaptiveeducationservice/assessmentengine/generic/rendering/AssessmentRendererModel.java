package com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.metadata.AssessmentMetadata;

import java.util.List;

public record AssessmentRendererModel(

        String code,

        String version,

        String title,

        String description,

        String instructions,

        AssessmentMetadata metadata,

        List<AssessmentRendererQuestion> questions
) {

    public AssessmentRendererModel {
        questions = questions == null
                ? List.of()
                : List.copyOf(questions);
    }
}