package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EducationalResilienceResponse(

        String resilienceLevel,

        String operationalContinuity,

        String adaptiveRecoveryCapability,

        String ethicalContainmentStatus,

        List<String> resilienceMechanisms,

        String institutionalStabilityStatus
) {
}
