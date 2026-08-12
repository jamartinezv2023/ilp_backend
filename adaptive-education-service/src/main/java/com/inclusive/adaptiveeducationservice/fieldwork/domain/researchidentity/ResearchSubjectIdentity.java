package com.inclusive.adaptiveeducationservice.fieldwork.domain.researchidentity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public final class ResearchSubjectIdentity {

    private final UUID participantUuid;
    private final ResearchSubjectId researchSubjectId;
    private final LocalDateTime createdAt;

    private LocalDateTime deactivatedAt;

    public ResearchSubjectIdentity(
            UUID participantUuid,
            ResearchSubjectId researchSubjectId
    ) {
        this(
                participantUuid,
                researchSubjectId,
                LocalDateTime.now(),
                null
        );
    }

    private ResearchSubjectIdentity(
            UUID participantUuid,
            ResearchSubjectId researchSubjectId,
            LocalDateTime createdAt,
            LocalDateTime deactivatedAt
    ) {
        this.participantUuid =
                Objects.requireNonNull(
                        participantUuid,
                        "participantUuid is required"
                );

        this.researchSubjectId =
                Objects.requireNonNull(
                        researchSubjectId,
                        "researchSubjectId is required"
                );

        this.createdAt =
                normalizeRequiredTimestamp(
                        createdAt,
                        "createdAt is required"
                );

        LocalDateTime normalizedDeactivatedAt =
                normalizeOptionalTimestamp(
                        deactivatedAt
                );

        if (
                normalizedDeactivatedAt != null
                        && normalizedDeactivatedAt.isBefore(
                                this.createdAt
                        )
        ) {
            throw new IllegalArgumentException(
                    "deactivatedAt must not be before createdAt"
            );
        }

        this.deactivatedAt =
                normalizedDeactivatedAt;
    }

    public static ResearchSubjectIdentity rehydrate(
            UUID participantUuid,
            ResearchSubjectId researchSubjectId,
            LocalDateTime createdAt,
            LocalDateTime deactivatedAt
    ) {
        return new ResearchSubjectIdentity(
                participantUuid,
                researchSubjectId,
                createdAt,
                deactivatedAt
        );
    }

    public void deactivate(
            LocalDateTime deactivationTime
    ) {
        if (!isActive()) {
            throw new IllegalStateException(
                    "Research subject identity is already inactive"
            );
        }

        LocalDateTime normalizedDeactivationTime =
                normalizeRequiredTimestamp(
                        deactivationTime,
                        "deactivationTime is required"
                );

        if (
                normalizedDeactivationTime.isBefore(
                        createdAt
                )
        ) {
            throw new IllegalArgumentException(
                    "deactivationTime must not be before createdAt"
            );
        }

        this.deactivatedAt =
                normalizedDeactivationTime;
    }

    public UUID getParticipantUuid() {
        return participantUuid;
    }

    public ResearchSubjectId getResearchSubjectId() {
        return researchSubjectId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public boolean isActive() {
        return deactivatedAt == null;
    }

    private static LocalDateTime normalizeRequiredTimestamp(
            LocalDateTime timestamp,
            String message
    ) {
        return Objects.requireNonNull(
                timestamp,
                message
        ).truncatedTo(
                ChronoUnit.MICROS
        );
    }

    private static LocalDateTime normalizeOptionalTimestamp(
            LocalDateTime timestamp
    ) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.truncatedTo(
                ChronoUnit.MICROS
        );
    }
}
