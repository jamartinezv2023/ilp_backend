package com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResearchSubjectIdentityTest {

    @Test
    void shouldAssociateParticipantWithResearchSubjectId() {
        UUID participantUuid =
                UUID.randomUUID();

        ResearchSubjectId researchSubjectId =
                ResearchSubjectId.generate();

        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        participantUuid,
                        researchSubjectId
                );

        assertThat(identity.getParticipantUuid())
                .isEqualTo(participantUuid);

        assertThat(identity.getResearchSubjectId())
                .isEqualTo(researchSubjectId);

        assertThat(identity.isActive())
                .isTrue();

        assertThat(identity.getCreatedAt())
                .isNotNull();

        assertThat(identity.getDeactivatedAt())
                .isNull();
    }

    @Test
    void shouldRejectNullParticipantUuid() {
        ResearchSubjectId researchSubjectId =
                ResearchSubjectId.generate();

        assertThatThrownBy(
                () -> new ResearchSubjectIdentity(
                        null,
                        researchSubjectId
                )
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessage(
                        "participantUuid is required"
                );
    }

    @Test
    void shouldRejectNullResearchSubjectId() {
        assertThatThrownBy(
                () -> new ResearchSubjectIdentity(
                        UUID.randomUUID(),
                        null
                )
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessage(
                        "researchSubjectId is required"
                );
    }

    @Test
    void shouldDeactivateActiveResearchIdentity() {
        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        UUID.randomUUID(),
                        ResearchSubjectId.generate()
                );

        LocalDateTime deactivationTime =
                identity.getCreatedAt()
                        .plusMinutes(1);

        identity.deactivate(
                deactivationTime
        );

        assertThat(identity.isActive())
                .isFalse();

        assertThat(identity.getDeactivatedAt())
                .isEqualTo(deactivationTime);
    }

    @Test
    void shouldRejectNullDeactivationTime() {
        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        UUID.randomUUID(),
                        ResearchSubjectId.generate()
                );

        assertThatThrownBy(
                () -> identity.deactivate(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessage(
                        "deactivationTime is required"
                );
    }

    @Test
    void shouldRejectDeactivationBeforeCreation() {
        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        UUID.randomUUID(),
                        ResearchSubjectId.generate()
                );

        LocalDateTime invalidTime =
                identity.getCreatedAt()
                        .minusSeconds(1);

        assertThatThrownBy(
                () -> identity.deactivate(
                        invalidTime
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "deactivationTime must not be before createdAt"
                );
    }

    @Test
    void shouldRejectRepeatedDeactivation() {
        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        UUID.randomUUID(),
                        ResearchSubjectId.generate()
                );

        identity.deactivate(
                identity.getCreatedAt()
                        .plusMinutes(1)
        );

        assertThatThrownBy(
                () -> identity.deactivate(
                        identity.getCreatedAt()
                                .plusMinutes(2)
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Research subject identity is already inactive"
                );
    }

    @Test
    void createsIdentityUsingMicrosecondTemporalPrecision() {
        ResearchSubjectIdentity identity =
                new ResearchSubjectIdentity(
                        UUID.randomUUID(),
                        ResearchSubjectId.generate()
                );

        assertThat(
                identity.getCreatedAt().getNano() % 1_000
        ).isZero();
    }

    @Test
    void rehydratesTemporalValuesUsingMicrosecondPrecision() {
        LocalDateTime createdAt =
                LocalDateTime.of(
                        2026,
                        8,
                        10,
                        10,
                        0,
                        0,
                        123_456_789
                );

        LocalDateTime deactivatedAt =
                LocalDateTime.of(
                        2026,
                        8,
                        10,
                        12,
                        0,
                        0,
                        987_654_321
                );

        ResearchSubjectIdentity identity =
                ResearchSubjectIdentity.rehydrate(
                        UUID.randomUUID(),
                        ResearchSubjectId.generate(),
                        createdAt,
                        deactivatedAt
                );

        assertThat(
                identity.getCreatedAt().getNano()
        ).isEqualTo(
                123_456_000
        );

        assertThat(
                identity.getDeactivatedAt().getNano()
        ).isEqualTo(
                987_654_000
        );
    }
}
