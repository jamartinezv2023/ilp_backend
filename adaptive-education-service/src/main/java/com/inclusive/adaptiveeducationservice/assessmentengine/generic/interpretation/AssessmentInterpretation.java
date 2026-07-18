package com.inclusive.adaptiveeducationservice.assessmentengine.generic.interpretation;

import java.util.List;
import java.util.Map;

public record AssessmentInterpretation(

        String primaryProfile,

        String secondaryProfile,

        String narrative,

        Map<String, String> dimensions,

        List<String> recommendations
) {

    public AssessmentInterpretation {
        dimensions = dimensions == null
                ? Map.of()
                : Map.copyOf(dimensions);

        recommendations = recommendations == null
                ? List.of()
                : List.copyOf(recommendations);

        narrative = narrative == null
                ? ""
                : narrative;
    }
}