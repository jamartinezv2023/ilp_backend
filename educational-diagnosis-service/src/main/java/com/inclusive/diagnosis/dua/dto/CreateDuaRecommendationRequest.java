package com.inclusive.diagnosis.dua.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDuaRecommendationRequest(

        UUID tenantId,

        UUID studentProfileId,

        @NotBlank
        String duaPrinciple,

        @NotBlank
        String recommendationCategory,

        @NotBlank
        String recommendationText,

        String accessibilitySupport,

        String assistiveTechnologySuggestion,

        String implementationGuidance,

        @NotNull
        Double priorityScore
) {
}
