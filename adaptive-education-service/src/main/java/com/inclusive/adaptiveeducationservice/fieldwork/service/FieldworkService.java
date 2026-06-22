package com.inclusive.adaptiveeducationservice.fieldwork.service;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ResearchParticipant;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ConsentRequest;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ConsentResponse;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ParticipantRequest;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ParticipantResponse;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FieldworkService {

    private final ConsentRecordRepository consentRepository;
    private final ResearchParticipantRepository participantRepository;

    public FieldworkService(
            ConsentRecordRepository consentRepository,
            ResearchParticipantRepository participantRepository
    ) {
        this.consentRepository = consentRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public ConsentResponse registerConsent(ConsentRequest request) {
        ConsentRecord saved = consentRepository.save(
                new ConsentRecord(
                        request.participantCode(),
                        request.consentType(),
                        request.status()
                )
        );

        return new ConsentResponse(
                saved.getConsentId(),
                saved.getParticipantCode(),
                saved.getConsentType(),
                saved.getStatus()
        );
    }

    @Transactional
    public ParticipantResponse createParticipant(ParticipantRequest request) {
        ConsentRecord consent = consentRepository
                .findFirstByParticipantCodeOrderByCreatedAtDesc(request.participantCode())
                .orElseThrow(() -> new IllegalStateException("Consent record not found"));

        if (!"APPROVED".equalsIgnoreCase(consent.getStatus())) {
            throw new IllegalStateException("Consent is not approved");
        }

        ResearchParticipant saved = participantRepository.save(
                new ResearchParticipant(
                        request.participantCode(),
                        consent.getStatus(),
                        request.cohortCode()
                )
        );

        return new ParticipantResponse(
                saved.getParticipantUuid(),
                saved.getParticipantCode(),
                saved.getConsentStatus(),
                saved.getCohortCode()
        );
    }
}
