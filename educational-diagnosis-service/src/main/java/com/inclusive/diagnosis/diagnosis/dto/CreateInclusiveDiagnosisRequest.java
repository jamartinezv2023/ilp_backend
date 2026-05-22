package com.inclusive.diagnosis.diagnosis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateInclusiveDiagnosisRequest(

        UUID tenantId,

        UUID studentProfileId,

        @NotBlank
        String diagnosisCategory,

        @NotBlank
        String diagnosisSummary,

        String identifiedBarriers,

        String learningStrengths,

        String supportNeeds,

        String recommendedInterventions,

        String dueAlignment,

        @NotNull
        Double confidenceScore
) {
}
