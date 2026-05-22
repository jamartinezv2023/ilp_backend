package com.inclusive.diagnosis.response.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateDiagnosticResponseRequest(

        UUID tenantId,

        UUID studentProfileId,

        UUID questionnaireId,

        @NotBlank
        String questionCode,

        @NotBlank
        String responseValue,

        String interpretationNotes
) {
}
