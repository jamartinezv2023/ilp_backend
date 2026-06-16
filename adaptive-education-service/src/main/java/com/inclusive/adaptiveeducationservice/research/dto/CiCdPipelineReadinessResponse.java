package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record CiCdPipelineReadinessResponse(

        String pipelineReadinessLevel,

        String buildAutomationStatus,

        String testAutomationStatus,

        String qualityGateAutomationStatus,

        String deploymentAutomationStatus,

        List<String> pipelineStages,

        String recommendedPipelineAction
) {
}
