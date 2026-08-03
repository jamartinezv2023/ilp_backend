package com.inclusive.adaptiveeducationservice.assessmentengine.generic.exception;

public class AssessmentStrategyNotFoundException
        extends RuntimeException {

    public AssessmentStrategyNotFoundException(String assessmentCode) {
        super(
                "No scoring strategy was registered for assessment: "
                        + assessmentCode
        );
    }
}
