package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record SupportDimensionResponse(

        String dimension,

        double confidence,

        List<String> observedSignals
) {
}
