package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record PsychopedagogicalProfileResponse(

        String studentProfileId,

        String kolbLearningStyle,

        String felderSilvermanProfile,

        String kuderVocationalPreference,

        String inclusiveSupportSummary,

        String recommendedEducationalPathway,

        List<String> mlReadinessSignals,

        String ethicalWarning
) {
}
