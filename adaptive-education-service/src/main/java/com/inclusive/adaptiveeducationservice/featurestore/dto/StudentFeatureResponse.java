package com.inclusive.adaptiveeducationservice.featurestore.dto;

import java.time.Instant;

public record StudentFeatureResponse(
        String id,
        String studentId,
        Instant featureDate,
        Integer kolbCe,
        Integer kolbRo,
        Integer kolbAc,
        Integer kolbAe,
        String kolbStyle
) {
}