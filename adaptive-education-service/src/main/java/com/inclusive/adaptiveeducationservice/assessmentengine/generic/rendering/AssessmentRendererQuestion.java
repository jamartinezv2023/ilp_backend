package com.inclusive.adaptiveeducationservice.assessmentengine.generic.rendering;

import java.util.List;

public record AssessmentRendererQuestion(

        String id,

        String code,

        String text,

        String dimension,

        String questionType,

        Boolean required,

        Integer orderIndex,

        List<AssessmentRendererOption> options
) {

    public AssessmentRendererQuestion {
        options = options == null
                ? List.of()
                : List.copyOf(options);
    }
}