package com.inclusive.adaptiveeducationservice.researchanalytics.dto;

public record KolbStatisticsResponse(
        Integer totalFeatureRows,
        Double averageCe,
        Double averageRo,
        Double averageAc,
        Double averageAe
) {
}