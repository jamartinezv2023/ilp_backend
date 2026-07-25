package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.model;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ParticipantAssessmentScientificHistory(
        String participantId,
        int totalObservations,
        Instant firstSubmittedAt,
        Instant lastSubmittedAt,
        List<AssessmentScientificObservation> observations
) {

    public ParticipantAssessmentScientificHistory {
        Objects.requireNonNull(
                participantId,
                "participantId is required"
        );

        observations = observations == null
                ? List.of()
                : List.copyOf(observations);

        if (totalObservations != observations.size()) {
            throw new IllegalArgumentException(
                    "totalObservations must match observations size"
            );
        }

        if (observations.isEmpty()) {
            if (
                    firstSubmittedAt != null
                            || lastSubmittedAt != null
            ) {
                throw new IllegalArgumentException(
                        "Empty history must not define temporal boundaries"
                );
            }
        } else {
            Objects.requireNonNull(
                    firstSubmittedAt,
                    "firstSubmittedAt is required for non-empty history"
            );

            Objects.requireNonNull(
                    lastSubmittedAt,
                    "lastSubmittedAt is required for non-empty history"
            );

            if (firstSubmittedAt.isAfter(lastSubmittedAt)) {
                throw new IllegalArgumentException(
                        "firstSubmittedAt must not be after lastSubmittedAt"
                );
            }
        }
    }

    public static ParticipantAssessmentScientificHistory empty(
            String participantId
    ) {
        return new ParticipantAssessmentScientificHistory(
                participantId,
                0,
                null,
                null,
                List.of()
        );
    }
}