package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.model.ParticipantAssessmentScientificHistory;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificHistoryQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

@Service
public class GetParticipantAssessmentScientificHistoryService {

    private static final Comparator<
            com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation
            > STABLE_HISTORY_ORDER =
            Comparator.comparing(
                    com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation
                            ::submittedAt,
                    Comparator.reverseOrder()
            ).thenComparing(
                    com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation
                            ::administrationId
            );

    private final AssessmentScientificHistoryQueryPort
            historyQueryPort;

    public GetParticipantAssessmentScientificHistoryService(
            AssessmentScientificHistoryQueryPort historyQueryPort
    ) {
        this.historyQueryPort = historyQueryPort;
    }

    @Transactional(readOnly = true)
    public ParticipantAssessmentScientificHistory
    getByParticipantId(
            String participantId
    ) {
        String normalizedParticipantId =
                Objects.requireNonNull(
                        participantId,
                        "participantId is required"
                ).trim();

        if (normalizedParticipantId.isEmpty()) {
            throw new IllegalArgumentException(
                    "participantId must not be blank"
            );
        }

        var observations =
                historyQueryPort
                        .findByParticipantId(
                                normalizedParticipantId
                        )
                        .stream()
                        .sorted(STABLE_HISTORY_ORDER)
                        .toList();

        if (observations.isEmpty()) {
            return ParticipantAssessmentScientificHistory.empty(
                    normalizedParticipantId
            );
        }

        Instant lastSubmittedAt =
                observations.get(0)
                        .submittedAt();

        Instant firstSubmittedAt =
                observations.get(
                        observations.size() - 1
                ).submittedAt();

        return new ParticipantAssessmentScientificHistory(
                normalizedParticipantId,
                observations.size(),
                firstSubmittedAt,
                lastSubmittedAt,
                observations
        );
    }
}