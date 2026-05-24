package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record SupportDimensionProfileResponse(

        String studentProfileId,

        List<SupportDimensionItem> supportDimensions,

        String ethicalWarning
) {

    public record SupportDimensionItem(

            String dimension,

            double confidence,

            List<String> observedSignals,

            String suggestedSupport
    ) {
    }
}
