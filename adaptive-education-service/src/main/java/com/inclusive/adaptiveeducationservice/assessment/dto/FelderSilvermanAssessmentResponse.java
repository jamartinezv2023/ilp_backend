package com.inclusive.adaptiveeducationservice.assessment.dto;

import java.time.Instant;
import java.util.List;

public record FelderSilvermanAssessmentResponse(

        String assessmentId,

        String studentId,

        Integer activeReflectiveScore,

        Integer sensingIntuitiveScore,

        Integer visualVerbalScore,

        Integer sequentialGlobalScore,

        String dominantProfile,

        List<String> learningPreferences,

        String instrumentVersion,

        Instant createdAt
) {
}