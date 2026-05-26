package com.inclusive.adaptiveeducationservice.research.dto;

public record TrustworthinessAssessmentResponse(

        String trustworthinessLevel,

        String explainabilityCompliance,

        String ethicalGovernanceStatus,

        String humanOversightRequirement,

        String riskContainmentLevel,

        String institutionalReliability
) {
}
