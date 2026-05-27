package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DevSecOpsQualityResponse(

        String ciPipelineStatus,

        String staticAnalysisStatus,

        String coverageReadiness,

        String securityAnalysisStatus,

        String qualityGateStatus,

        List<String> devSecOpsEvidence,

        String recommendedQualityAction
) {
}
