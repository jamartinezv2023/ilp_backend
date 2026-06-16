package com.inclusive.adaptiveeducationservice.assessmenthistory.dto;

import java.time.Instant;

public record AssessmentHistoryItemResponse(
        String responseId,
        String studentId,
        String assessmentCode,
        String assessmentVersion,
        String status,
        Integer answerCount,
        Integer totalScore,
        Instant submittedAt
) {
}