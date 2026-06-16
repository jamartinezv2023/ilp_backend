package com.inclusive.adaptiveeducationservice.assessment.dto;

import java.time.Instant;
import java.util.List;

public record KuderAssessmentResponse(

        String assessmentId,

        String studentId,

        String dominantVocationalArea,

        List<String> topVocationalAreas,

        Integer scientificScore,

        Integer artisticScore,

        Integer socialScore,

        Integer mechanicalScore,

        Integer administrativeScore,

        String instrumentVersion,

        Instant createdAt
) {
}