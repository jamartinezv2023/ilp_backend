package com.inclusive.adaptiveeducationservice.fieldwork.dto;

public record ConsentRequest(
        String participantCode,
        String consentType,
        String status
) {
}
