package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record AdaptivePiarPlanResponse(

        String studentProfileId,

        String supportNeedArea,

        String detectedBarrier,

        String reasonableAdjustment,

        String duaStrategy,

        String evaluationAdaptation,

        String followUpRecommendation,

        List<String> supportCategories,

        String ethicalWarning
) {
}
