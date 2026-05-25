package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record InterventionOrchestrationPreviewResponse(

        String studentId,

        String currentPhase,

        List<String> recommendedInterventionOrder,

        String adaptiveAdjustmentTrigger,

        double orchestrationConfidence,

        String ethicalOrchestrationPolicy
) {
}
