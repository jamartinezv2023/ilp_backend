package com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;

import java.util.Optional;

public interface AssessmentScientificObservationQueryPort {

    Optional<AssessmentScientificObservation>
    findByAdministrationId(
            String administrationId
    );
}