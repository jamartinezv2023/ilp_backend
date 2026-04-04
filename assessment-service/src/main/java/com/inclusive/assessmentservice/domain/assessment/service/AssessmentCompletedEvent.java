package com.inclusive.assessmentservice.domain.assessment.service;

public record AssessmentCompletedEvent(
    String assessmentId,
    String userId,
    Double score
) {}
