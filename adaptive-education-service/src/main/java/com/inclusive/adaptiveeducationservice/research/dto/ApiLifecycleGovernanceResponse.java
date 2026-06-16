package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ApiLifecycleGovernanceResponse(

        String lifecycleGovernanceStatus,

        String apiPublicationStatus,

        String apiEvolutionReadiness,

        String deprecationGovernance,

        String auditabilityLevel,

        List<String> lifecycleEvidence,

        String recommendedLifecycleAction
) {
}
