package com.inclusive.adaptiveeducationservice.api.assessmentsubmission;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record SubmitAssessmentResponse(

        String administrationId,

        String participantId,

        String assessmentCode,

        String assessmentVersion,

        String status,

        String primaryProfile,

        Map<String, Double> scores,

        Map<String, String> interpretations,

        List<String> recommendations,

        String scoringAlgorithmVersion,

        Integer persistedAnswerCount,

        Instant calculatedAt
) {
}