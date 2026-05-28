package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ArchitectureRemediationRoadmapResponse(

        String roadmapStatus,

        String priorityLevel,

        List<String> criticalRemediationItems,

        List<String> mediumTermHardeningItems,

        List<String> evidenceToProduce,

        String recommendedNextEngineeringSprint
) {
}
