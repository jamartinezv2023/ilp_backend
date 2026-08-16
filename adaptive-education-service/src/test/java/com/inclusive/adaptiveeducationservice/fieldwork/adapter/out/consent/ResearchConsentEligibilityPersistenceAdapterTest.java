package com.inclusive.adaptiveeducationservice.fieldwork.adapter.out.consent;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ResearchParticipant;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ResearchConsentEligibilityPersistenceAdapterTest {

    @Mock
    private ResearchParticipantRepository participantRepository;

    @Mock
    private ConsentRecordRepository consentRepository;

    private ResearchConsentEligibilityPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adapter =
                new ResearchConsentEligibilityPersistenceAdapter(
                        participantRepository,
                        consentRepository
                );
    }

    @Test
    void returnsTrueWhenLatestConsentIsApproved() {
        ResearchParticipant participant =
                new ResearchParticipant(
                        "P-001",
                        "APPROVED",
                        "COHORT-1"
                );

        UUID participantUuid =
                participant.getParticipantUuid();

        ConsentRecord consent =
                new ConsentRecord(
                        "P-001",
                        "RESEARCH",
                        "APPROVED"
                );

        when(participantRepository.findById(participantUuid))
                .thenReturn(Optional.of(participant));

        when(consentRepository
                .findFirstByParticipantCodeOrderByCreatedAtDesc(
                        "P-001"
                ))
                .thenReturn(Optional.of(consent));

        assertThat(
                adapter.hasActiveConsent(participantUuid)
        ).isTrue();
    }

    @Test
    void returnsFalseWhenLatestConsentIsWithdrawn() {
        ResearchParticipant participant =
                new ResearchParticipant(
                        "P-002",
                        "APPROVED",
                        "COHORT-1"
                );

        UUID participantUuid =
                participant.getParticipantUuid();

        ConsentRecord consent =
                new ConsentRecord(
                        "P-002",
                        "RESEARCH",
                        "APPROVED"
                );

        consent.withdraw(
                consent.getApprovedAt().plusMinutes(1)
        );

        when(participantRepository.findById(participantUuid))
                .thenReturn(Optional.of(participant));

        when(consentRepository
                .findFirstByParticipantCodeOrderByCreatedAtDesc(
                        "P-002"
                ))
                .thenReturn(Optional.of(consent));

        assertThat(
                adapter.hasActiveConsent(participantUuid)
        ).isFalse();
    }

    @Test
    void returnsFalseWhenParticipantDoesNotExist() {
        UUID participantUuid =
                UUID.randomUUID();

        when(participantRepository.findById(participantUuid))
                .thenReturn(Optional.empty());

        assertThat(
                adapter.hasActiveConsent(participantUuid)
        ).isFalse();
    }

    @Test
    void returnsFalseWhenParticipantHasNoConsentRecord() {
        ResearchParticipant participant =
                new ResearchParticipant(
                        "P-003",
                        "APPROVED",
                        "COHORT-1"
                );

        UUID participantUuid =
                participant.getParticipantUuid();

        when(participantRepository.findById(participantUuid))
                .thenReturn(Optional.of(participant));

        when(consentRepository
                .findFirstByParticipantCodeOrderByCreatedAtDesc(
                        "P-003"
                ))
                .thenReturn(Optional.empty());

        assertThat(
                adapter.hasActiveConsent(participantUuid)
        ).isFalse();
    }
}
