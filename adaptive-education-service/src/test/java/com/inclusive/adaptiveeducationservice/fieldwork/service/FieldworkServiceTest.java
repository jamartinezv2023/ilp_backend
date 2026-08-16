package com.inclusive.adaptiveeducationservice.fieldwork.service;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.domain.ResearchParticipant;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ConsentResponse;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ParticipantRequest;
import com.inclusive.adaptiveeducationservice.fieldwork.dto.ParticipantResponse;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldworkServiceTest {

    @Mock
    private ConsentRecordRepository consentRepository;

    @Mock
    private ResearchParticipantRepository participantRepository;

    private FieldworkService service;

    @BeforeEach
    void setUp() {
        service =
                new FieldworkService(
                        consentRepository,
                        participantRepository
                );
    }

    @Test
    void createsParticipantWithoutPriorConsent() {
        ParticipantRequest request =
                new ParticipantRequest(
                        "P-NO-CONSENT",
                        "PILOT-1"
                );

        when(
                consentRepository
                        .findFirstByParticipantCodeOrderByCreatedAtDesc(
                                "P-NO-CONSENT"
                        )
        ).thenReturn(
                Optional.empty()
        );

        when(
                participantRepository.save(
                        any(ResearchParticipant.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        ParticipantResponse result =
                service.createParticipant(
                        request
                );

        assertThat(
                result.participantUuid()
        ).isNotNull();

        assertThat(
                result.participantCode()
        ).isEqualTo(
                "P-NO-CONSENT"
        );

        assertThat(
                result.consentStatus()
        ).isNull();

        assertThat(
                result.cohortCode()
        ).isEqualTo(
                "PILOT-1"
        );
    }

    @Test
    void withdrawsApprovedConsent() {

        ConsentRecord consent =
                new ConsentRecord(
                        "PARTICIPANT-WITHDRAW-001",
                        "RESEARCH",
                        "APPROVED"
                );

        UUID consentId =
                consent.getConsentId();

        when(
                consentRepository.findById(
                        consentId
                )
        ).thenReturn(
                Optional.of(consent)
        );

        when(
                consentRepository.save(
                        any(ConsentRecord.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        ConsentResponse result =
                service.withdrawConsent(
                        consentId
                );

        assertThat(
                result.consentId()
        ).isEqualTo(
                consentId
        );

        assertThat(
                result.participantCode()
        ).isEqualTo(
                "PARTICIPANT-WITHDRAW-001"
        );

        assertThat(
                result.consentType()
        ).isEqualTo(
                "RESEARCH"
        );

        assertThat(
                result.status()
        ).isEqualTo(
                "WITHDRAWN"
        );

        assertThat(
                consent.getStatus()
        ).isEqualTo(
                "WITHDRAWN"
        );

        assertThat(
                consent.getWithdrawnAt()
        ).isNotNull();

        verify(
                consentRepository
        ).save(
                consent
        );
    }

    @Test
    void rejectsUnknownConsentWithdrawal() {

        UUID consentId =
                UUID.randomUUID();

        when(
                consentRepository.findById(
                        consentId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () ->
                        service.withdrawConsent(
                                consentId
                        )
        )
                .isInstanceOf(
                        ConsentRecordNotFoundException.class
                )
                .hasMessageContaining(
                        consentId.toString()
                );
    }
}
