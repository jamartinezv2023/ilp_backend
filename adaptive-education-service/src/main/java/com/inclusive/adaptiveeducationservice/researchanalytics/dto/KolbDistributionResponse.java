package com.inclusive.adaptiveeducationservice.researchanalytics.dto;

import java.util.Map;

public record KolbDistributionResponse(
        Integer totalFeatureRows,
        Map<String, Long> distribution
) {
}