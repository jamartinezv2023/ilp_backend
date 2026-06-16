package com.inclusive.adaptiveeducationservice.assessment.dto;

import java.time.Instant;

public record KolbAssessmentResponse(

        String assessmentId,

        String studentId,

        Integer scoreCE,

        Integer scoreRO,

        Integer scoreAC,

        Integer scoreAE,

        String learningStyle,

        String instrumentVersion,

        Instant createdAt
) {
}