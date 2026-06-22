package com.inclusive.adaptiveeducationservice.fieldwork.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fieldwork_research_participants")
public class ResearchParticipant {

    @Id
    private UUID participantUuid;

    private String participantCode;
    private String consentStatus;
    private String cohortCode;
    private LocalDateTime createdAt;

    protected ResearchParticipant() {
    }

    public ResearchParticipant(String participantCode, String consentStatus, String cohortCode) {
        this.participantUuid = UUID.randomUUID();
        this.participantCode = participantCode;
        this.consentStatus = consentStatus;
        this.cohortCode = cohortCode;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getParticipantUuid() { return participantUuid; }
    public String getParticipantCode() { return participantCode; }
    public String getConsentStatus() { return consentStatus; }
    public String getCohortCode() { return cohortCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
