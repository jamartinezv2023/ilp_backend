package com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception;

public class AssessmentDefinitionNotFoundException
        extends RuntimeException {

    public AssessmentDefinitionNotFoundException(
            String assessmentCode,
            String version
    ) {
        super(
                "Assessment definition was not found: "
                        + assessmentCode
                        + " version "
                        + version
        );
    }

    public AssessmentDefinitionNotFoundException(
            String assessmentCode
    ) {
        super(
                "No active assessment definition was found for: "
                        + assessmentCode
        );
    }
}