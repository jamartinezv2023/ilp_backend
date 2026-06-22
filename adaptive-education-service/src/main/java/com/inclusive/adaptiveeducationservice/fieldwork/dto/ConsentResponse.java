package com.inclusive.adaptiveeducationservice.fieldwork.dto;

import java.util.UUID;

public record ConsentResponse(
        UUID consentId,
        String participantCode,
        String consentType,
        String status
) {
}
