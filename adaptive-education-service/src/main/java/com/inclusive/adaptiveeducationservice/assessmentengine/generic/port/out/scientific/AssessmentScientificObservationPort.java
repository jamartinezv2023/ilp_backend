package com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.PersistAssessmentScientificObservationCommand;

public interface AssessmentScientificObservationPort {

    void save(
            PersistAssessmentScientificObservationCommand command
    );

    boolean existsByAdministrationId(
            String administrationId
    );
}