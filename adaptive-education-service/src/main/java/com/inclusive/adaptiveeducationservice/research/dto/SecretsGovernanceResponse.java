package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record SecretsGovernanceResponse(

        String secretsGovernanceStatus,

        String credentialExposureRisk,

        String environmentVariablePolicy,

        String ciCdSecretsReadiness,

        String repositoryProtectionStatus,

        List<String> secretsGovernanceEvidence,

        String recommendedSecretsAction
) {
}
