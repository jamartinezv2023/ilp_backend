package com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.query.model.AssessmentScientificObservation;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.AssessmentScientificObservationQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class GetAssessmentScientificObservationService {

    private final AssessmentScientificObservationQueryPort
            queryPort;

    public GetAssessmentScientificObservationService(
            AssessmentScientificObservationQueryPort queryPort
    ) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public AssessmentScientificObservation getByAdministrationId(
            String administrationId
    ) {
        String normalizedAdministrationId =
                Objects.requireNonNull(
                        administrationId,
                        "administrationId is required"
                ).trim();

        if (normalizedAdministrationId.isEmpty()) {
            throw new IllegalArgumentException(
                    "administrationId must not be blank"
            );
        }

        return queryPort
                .findByAdministrationId(
                        normalizedAdministrationId
                )
                .orElseThrow(() ->
                        new AssessmentScientificObservationNotFoundException(
                                normalizedAdministrationId
                        )
                );
    }
}