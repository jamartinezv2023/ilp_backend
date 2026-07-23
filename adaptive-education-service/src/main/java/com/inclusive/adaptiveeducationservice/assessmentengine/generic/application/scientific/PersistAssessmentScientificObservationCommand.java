package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentSubmission;

import java.util.Objects;

public record PersistAssessmentScientificObservationCommand(
        AssessmentSubmission submission,
        AssessmentResult result
) {

    public PersistAssessmentScientificObservationCommand {
        Objects.requireNonNull(
                submission,
                "submission is required"
        );

        Objects.requireNonNull(
                result,
                "result is required"
        );

        if (!submission.administrationId().equals(
                result.administrationId()
        )) {
            throw new IllegalArgumentException(
                    "Submission and result administration ids must match"
            );
        }

        if (!submission.participantId().equals(
                result.participantId()
        )) {
            throw new IllegalArgumentException(
                    "Submission and result participant ids must match"
            );
        }

        if (!submission.assessmentCode().equals(
                result.assessmentCode()
        )) {
            throw new IllegalArgumentException(
                    "Submission and result assessment codes must match"
            );
        }
    }
}