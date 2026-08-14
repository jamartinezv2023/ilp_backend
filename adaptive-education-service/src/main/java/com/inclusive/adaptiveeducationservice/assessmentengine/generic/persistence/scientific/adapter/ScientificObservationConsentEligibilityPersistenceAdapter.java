package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificObservationConsentEligibilityPort;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class ScientificObservationConsentEligibilityPersistenceAdapter
        implements ScientificObservationConsentEligibilityPort {

    private final ConsentRecordRepository consentRepository;

    public ScientificObservationConsentEligibilityPersistenceAdapter(
            ConsentRecordRepository consentRepository
    ) {
        this.consentRepository =
                Objects.requireNonNull(
                        consentRepository,
                        "consentRepository is required"
                );
    }

    @Override
    public boolean hasActiveConsentForParticipantCode(
            String participantCode
    ) {
        String requiredParticipantCode =
                Objects.requireNonNull(
                        participantCode,
                        "participantCode is required"
                ).trim();

        return !requiredParticipantCode.isEmpty()
                && consentRepository
                .findFirstByParticipantCodeOrderByCreatedAtDesc(
                        requiredParticipantCode
                )
                .map(ConsentRecord::getStatus)
                .map(status ->
                        "APPROVED".equalsIgnoreCase(status)
                )
                .orElse(false);
    }
}
