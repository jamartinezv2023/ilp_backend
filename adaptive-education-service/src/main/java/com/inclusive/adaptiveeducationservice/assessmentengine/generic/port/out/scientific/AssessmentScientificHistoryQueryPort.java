package com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;

import java.util.List;

public interface AssessmentScientificHistoryQueryPort {

    List<AssessmentScientificObservation>
    findByParticipantId(
            String participantId
    );
}