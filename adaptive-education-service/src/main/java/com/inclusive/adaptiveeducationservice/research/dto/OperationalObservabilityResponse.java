package com.inclusive.adaptiveeducationservice.research.dto;

public record OperationalObservabilityResponse(

        String observabilityLevel,

        String decisionMonitoring,

        String adaptiveEventTracking,

        String institutionalAuditStatus,

        String longitudinalMonitoring,

        String operationalRiskStatus
) {
}
