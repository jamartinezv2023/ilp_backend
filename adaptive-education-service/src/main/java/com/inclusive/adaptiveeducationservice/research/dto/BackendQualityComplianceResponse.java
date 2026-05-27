package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record BackendQualityComplianceResponse(

        String architectureStyle,

        String idlContractStatus,

        String backendDeploymentStatus,

        String apiRestCompliance,

        String containerizationReadiness,

        String qualityGateReadiness,

        List<String> evaluationEvidence,

        String recommendedEngineeringAction
) {
}
