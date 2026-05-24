package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record EarlySupportAlertResponse(

        String studentProfileId,

        String alertLevel,

        String alertType,

        double supportNeedScore,

        List<String> observedSignals,

        String ethicalWarning,

        String recommendedTeacherAction,

        String piarRecommendation
) {
}
