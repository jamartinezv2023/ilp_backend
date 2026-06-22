package com.inclusive.adaptiveeducationservice.fieldwork.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fieldwork_consent_records")
public class ConsentRecord {

    @Id
    private UUID consentId;

    private String participantCode;
    private String consentType;
    private String status;
    private LocalDateTime approvedAt;
    private LocalDateTime withdrawnAt;
    private LocalDateTime createdAt;

    protected ConsentRecord() {
    }

    public ConsentRecord(String participantCode, String consentType, String status) {
        this.consentId = UUID.randomUUID();
        this.participantCode = participantCode;
        this.consentType = consentType;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        if ("APPROVED".equalsIgnoreCase(status)) {
            this.approvedAt = LocalDateTime.now();
        }
    }

    public UUID getConsentId() { return consentId; }
    public String getParticipantCode() { return participantCode; }
    public String getConsentType() { return consentType; }
    public String getStatus() { return status; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public LocalDateTime getWithdrawnAt() { return withdrawnAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
