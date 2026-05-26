package com.inclusive.adaptiveeducationservice.research.dto;

public record EducationalComplianceResponse(

        String complianceLevel,

        String ethicalCompliance,

        String inclusiveEducationCompliance,

        String humanOversightCompliance,

        String traceabilityCompliance,

        String institutionalGovernanceCompliance
) {
}
