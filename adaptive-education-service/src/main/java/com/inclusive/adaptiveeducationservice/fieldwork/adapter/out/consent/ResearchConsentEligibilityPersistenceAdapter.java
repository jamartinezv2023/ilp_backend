package com.inclusive.adaptiveeducationservice.fieldwork.adapter.out.consent;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ResearchParticipant;
import com.inclusive.adaptiveeducationservice.fieldwork.port.out.researchidentity.ResearchConsentEligibilityQueryPort;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public final class ResearchConsentEligibilityPersistenceAdapter
        implements ResearchConsentEligibilityQueryPort {

    private final ResearchParticipantRepository participantRepository;

    private final ConsentRecordRepository consentRepository;

    public ResearchConsentEligibilityPersistenceAdapter(
            ResearchParticipantRepository participantRepository,
            ConsentRecordRepository consentRepository
    ) {
        this.participantRepository =
                Objects.requireNonNull(
                        participantRepository,
                        "participantRepository is required"
                );

        this.consentRepository =
                Objects.requireNonNull(
                        consentRepository,
                        "consentRepository is required"
                );
    }

    @Override
    public boolean hasActiveConsent(
            UUID participantUuid
    ) {
        UUID requiredParticipantUuid =
                Objects.requireNonNull(
                        participantUuid,
                        "participantUuid is required"
                );

        Optional<ResearchParticipant> participant =
                participantRepository.findById(
                        requiredParticipantUuid
                );

        if (participant.isEmpty()) {
            return false;
        }

        Optional<ConsentRecord> latestConsent =
                consentRepository
                        .findFirstByParticipantCodeOrderByCreatedAtDesc(
                                participant
                                        .orElseThrow()
                                        .getParticipantCode()
                        );

        return latestConsent
                .map(ConsentRecord::getStatus)
                .map(status ->
                        "APPROVED".equalsIgnoreCase(status)
                )
                .orElse(false);
    }
}
