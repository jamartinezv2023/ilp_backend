package com.inclusive.adaptiveeducationservice.fieldwork.adapter.out.persistence.researchidentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "fieldwork_research_subject_identities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_research_identity_participant",
                        columnNames = "participant_uuid"
                ),
                @UniqueConstraint(
                        name = "uq_research_identity_subject",
                        columnNames = "research_subject_id"
                )
        }
)
public final class ResearchSubjectIdentityEntity {

    @Id
    @Column(
            name = "id",
            nullable = false
    )
    private UUID id;

    @Column(
            name = "participant_uuid",
            nullable = false
    )
    private UUID participantUuid;

    @Column(
            name = "research_subject_id",
            nullable = false
    )
    private UUID researchSubjectId;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "deactivated_at"
    )
    private LocalDateTime deactivatedAt;

    protected ResearchSubjectIdentityEntity() {
    }

    public ResearchSubjectIdentityEntity(
            UUID participantUuid,
            UUID researchSubjectId,
            LocalDateTime createdAt,
            LocalDateTime deactivatedAt
    ) {
        this(
                UUID.randomUUID(),
                participantUuid,
                researchSubjectId,
                createdAt,
                deactivatedAt
        );
    }

    public ResearchSubjectIdentityEntity(
            UUID id,
            UUID participantUuid,
            UUID researchSubjectId,
            LocalDateTime createdAt,
            LocalDateTime deactivatedAt
    ) {
        this.id =
                Objects.requireNonNull(
                        id,
                        "id is required"
                );

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
                Objects.requireNonNull(
                        createdAt,
                        "createdAt is required"
                );

        if (
                deactivatedAt != null
                        && deactivatedAt.isBefore(
                                this.createdAt
                        )
        ) {
            throw new IllegalArgumentException(
                    "deactivatedAt must not be before createdAt"
            );
        }

        this.deactivatedAt =
                deactivatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getParticipantUuid() {
        return participantUuid;
    }

    public UUID getResearchSubjectId() {
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
}
