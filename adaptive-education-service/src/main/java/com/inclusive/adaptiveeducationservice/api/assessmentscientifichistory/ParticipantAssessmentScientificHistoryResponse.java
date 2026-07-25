package com.inclusive.adaptiveeducationservice.api.assessmentscientifichistory;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;

import java.time.Instant;
import java.util.List;

public record ParticipantAssessmentScientificHistoryResponse(
        String participantId,
        int totalObservations,
        Instant firstSubmittedAt,
        Instant lastSubmittedAt,
        List<AssessmentScientificObservation> observations
) {
}