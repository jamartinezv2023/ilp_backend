package com.inclusive.adaptiveeducationservice.fieldwork.dto;

import java.util.UUID;

public record ParticipantResponse(
        UUID participantUuid,
        String participantCode,
        String consentStatus,
        String cohortCode
) {
}
