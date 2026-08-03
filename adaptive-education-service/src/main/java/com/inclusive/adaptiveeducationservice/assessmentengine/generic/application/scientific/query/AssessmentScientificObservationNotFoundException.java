package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query;

public class AssessmentScientificObservationNotFoundException
        extends RuntimeException {

    public AssessmentScientificObservationNotFoundException(
            String administrationId
    ) {
        super(
                "Scientific assessment observation not found: "
                        + administrationId
        );
    }
}