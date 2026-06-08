package com.inclusive.adaptiveeducationservice.dataset.dto;

import java.time.Instant;

public record EducationalMlTrainingRowResponse(
        String studentId,
        Instant featureDate,
        Integer kolbCe,
        Integer kolbRo,
        Integer kolbAc,
        Integer kolbAe,
        String kolbStyle
) {
}