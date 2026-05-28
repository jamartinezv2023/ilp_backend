package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record OpenApiVersioningResponse(

        String contractVersioningStatus,

        String currentContractVersion,

        String backwardCompatibilityPolicy,

        String breakingChangeControl,

        List<String> versioningEvidence,

        String recommendedVersioningAction
) {
}
