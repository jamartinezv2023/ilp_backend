package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record RepositoryHygieneResponse(

        String repositoryHygieneStatus,

        String gitIgnoreReadiness,

        String artifactControlStatus,

        String sensitiveFileProtection,

        String traceabilityStatus,

        List<String> hygieneEvidence,

        String recommendedRepositoryAction
) {
}
