package com.inclusive.adaptiveeducationservice.research.dto;

public record AccountabilityResponsibilityResponse(

        String accountabilityLevel,

        String responsibleActor,

        String aiDecisionRole,

        String finalDecisionAuthority,

        String auditTrailRequirement,

        String institutionalResponsibilityStatus
) {
}
