package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record BoundedContextModularDesignResponse(

        String modularDesignLevel,

        String boundedContextStatus,

        List<String> identifiedBoundedContexts,

        String responsibilitySeparationStatus,

        String domainAlignmentStatus,

        String recommendedModularAction
) {
}
