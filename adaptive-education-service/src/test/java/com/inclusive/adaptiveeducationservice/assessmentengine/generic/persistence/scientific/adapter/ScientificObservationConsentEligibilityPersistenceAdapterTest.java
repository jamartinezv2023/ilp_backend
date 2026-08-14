package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence.scientific.adapter;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScientificObservationConsentEligibilityPersistenceAdapterTest {

    private ConsentRecordRepository consentRepository;

    private ScientificObservationConsentEligibilityPersistenceAdapter
            adapter;

    @BeforeEach
    void setUp() {

        consentRepository =
                mock(
                        ConsentRecordRepository.class
                );

        adapter =
                new ScientificObservationConsentEligibilityPersistenceAdapter(
                        consentRepository
                );
    }

    @Test
    void allowsScientificObservationWhenLatestConsentIsApproved() {

        ConsentRecord consent =
                new ConsentRecord(
                        "ST-001",
                        "RESEARCH",
                        "APPROVED"
                );

        when(
                consentRepository
                        .findFirstByParticipantCodeOrderByCreatedAtDesc(
                                "ST-001"
                        )
        ).thenReturn(
                Optional.of(consent)
        );

        boolean eligible =
                adapter
                        .hasActiveConsentForParticipantCode(
                                "ST-001"
                        );

        assertThat(
                eligible
        ).isTrue();

        verify(
                consentRepository
        ).findFirstByParticipantCodeOrderByCreatedAtDesc(
                "ST-001"
        );
    }

    @Test
    void rejectsScientificObservationWhenLatestConsentIsWithdrawn() {

        ConsentRecord consent =
                new ConsentRecord(
                        "ST-001",
                        "RESEARCH",
                        "APPROVED"
                );

        consent.withdraw(
                LocalDateTime.now()
                        .plusSeconds(1)
        );

        when(
                consentRepository
                        .findFirstByParticipantCodeOrderByCreatedAtDesc(
                                "ST-001"
                        )
        ).thenReturn(
                Optional.of(consent)
        );

        boolean eligible =
                adapter
                        .hasActiveConsentForParticipantCode(
                                "ST-001"
                        );

        assertThat(
                eligible
        ).isFalse();
    }

    @Test
    void rejectsScientificObservationWhenNoConsentExists() {

        when(
                consentRepository
                        .findFirstByParticipantCodeOrderByCreatedAtDesc(
                                "ST-001"
                        )
        ).thenReturn(
                Optional.empty()
        );

        boolean eligible =
                adapter
                        .hasActiveConsentForParticipantCode(
                                "ST-001"
                        );

        assertThat(
                eligible
        ).isFalse();
    }

    @Test
    void rejectsBlankParticipantCodeWithoutRepositoryLookup() {

        boolean eligible =
                adapter
                        .hasActiveConsentForParticipantCode(
                                "   "
                        );

        assertThat(
                eligible
        ).isFalse();
    }
}