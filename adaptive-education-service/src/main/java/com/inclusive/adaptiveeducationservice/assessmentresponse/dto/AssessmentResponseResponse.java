package com.inclusive.adaptiveeducationservice.assessmentresponse.dto;

import java.time.Instant;
import java.util.List;

public record AssessmentResponseResponse(
        String id,
        String studentId,
        String assessmentCode,
        String assessmentVersion,
        String status,
        Instant submittedAt,
        List<AssessmentAnswerResponse> answers
) {
}