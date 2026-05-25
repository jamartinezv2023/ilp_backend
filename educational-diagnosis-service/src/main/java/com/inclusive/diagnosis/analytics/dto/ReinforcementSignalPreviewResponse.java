package com.inclusive.diagnosis.analytics.dto;

public record ReinforcementSignalPreviewResponse(

        String studentId,

        double rewardSignal,

        String rewardReason,

        String recommendedNextAction,

        String policyFeedback,

        String ethicalRewardPolicy
) {
}
