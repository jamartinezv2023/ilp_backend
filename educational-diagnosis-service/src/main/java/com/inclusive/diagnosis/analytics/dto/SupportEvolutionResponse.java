package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record SupportEvolutionResponse(

        String studentProfileId,

        String mainSupportDimension,

        String trend,

        String evolution,

        String recommendedAction,

        List<String> evidence
) {
}
