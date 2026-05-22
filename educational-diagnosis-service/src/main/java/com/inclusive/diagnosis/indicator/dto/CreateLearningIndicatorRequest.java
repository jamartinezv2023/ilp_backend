package com.inclusive.diagnosis.indicator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLearningIndicatorRequest(

        UUID tenantId,

        UUID studentProfileId,

        @NotBlank
        String indicatorCode,

        @NotBlank
        String indicatorCategory,

        @NotNull
        Double indicatorValue,

        String interpretation,

        String pedagogicalRecommendation
) {
}
