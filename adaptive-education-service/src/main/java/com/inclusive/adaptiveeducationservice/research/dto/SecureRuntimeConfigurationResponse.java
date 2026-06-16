package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record SecureRuntimeConfigurationResponse(

        String runtimeConfigurationStatus,

        String secretManagementStatus,

        String environmentSeparationStatus,

        String datasourceSecurityStatus,

        String profileGovernanceStatus,

        List<String> configurationEvidence,

        String recommendedConfigurationAction
) {
}
