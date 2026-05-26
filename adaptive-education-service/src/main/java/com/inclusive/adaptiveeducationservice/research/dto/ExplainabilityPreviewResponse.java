package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ExplainabilityPreviewResponse(

        String decisionId,

        List<String> primaryFactors,

        String recommendedSupport,

        String explainabilityLevel,

        String institutionalInterpretability
) {
}
