package com.inclusive.adaptiveeducationservice.api.assessmentscientificobservation;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificInterpretation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificScoreItem;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.ScientificSubmissionContext;

import java.time.Instant;
import java.util.List;

public record AssessmentScientificObservationResponse(
        String administrationId,
        String participantId,
        String assessmentCode,
        String assessmentVersion,
        String primaryProfile,
        String scoringAlgorithmVersion,
        String interpretationVersion,
        Instant calculatedAt,
        Instant submittedAt,
        Instant featureCutoffAt,
        List<ScientificScoreItem> scores,
        List<ScientificInterpretation> interpretations,
        ScientificSubmissionContext context
) {
}