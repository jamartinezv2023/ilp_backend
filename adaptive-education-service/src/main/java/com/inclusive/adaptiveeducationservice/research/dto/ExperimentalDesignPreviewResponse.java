package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ExperimentalDesignPreviewResponse(

        String researchType,

        String experimentalModel,

        String population,

        String samplingStrategy,

        List<String> evaluationDimensions,

        String ethicalCompliance
) {
}
