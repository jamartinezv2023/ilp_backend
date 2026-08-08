package com.inclusive.adaptiveeducationservice.fieldwork.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsentRecordTest {

    @Test
    void shouldWithdrawApprovedConsent() {
        ConsentRecord consent =
                new ConsentRecord(
                        "PARTICIPANT-001",
                        "RESEARCH",
                        "APPROVED"
                );

        LocalDateTime withdrawalTime =
                consent.getApprovedAt().plusMinutes(1);

        consent.withdraw(withdrawalTime);

        assertThat(consent.getStatus())
                .isEqualTo("WITHDRAWN");

        assertThat(consent.getWithdrawnAt())
                .isEqualTo(withdrawalTime);
    }

    @Test
    void shouldRejectWithdrawalOfNonApprovedConsent() {
        ConsentRecord consent =
                new ConsentRecord(
                        "PARTICIPANT-002",
                        "RESEARCH",
                        "PENDING"
                );

        assertThatThrownBy(
                () -> consent.withdraw(
                        LocalDateTime.now()
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Only approved consent can be withdrawn"
                );
    }

    @Test
    void shouldRejectNullWithdrawalTime() {
        ConsentRecord consent =
                new ConsentRecord(
                        "PARTICIPANT-003",
                        "RESEARCH",
                        "APPROVED"
                );

        assertThatThrownBy(
                () -> consent.withdraw(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessage(
                        "withdrawalTime is required"
                );
    }

    @Test
    void shouldRejectWithdrawalBeforeApprovalTime() {
        ConsentRecord consent =
                new ConsentRecord(
                        "PARTICIPANT-004",
                        "RESEARCH",
                        "APPROVED"
                );

        LocalDateTime invalidWithdrawalTime =
                consent.getApprovedAt().minusSeconds(1);

        assertThatThrownBy(
                () -> consent.withdraw(
                        invalidWithdrawalTime
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "withdrawalTime must not be before approvedAt"
                );
    }
}