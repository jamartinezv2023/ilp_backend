package com.inclusive.adaptiveeducationservice.assessmentresponse.dto;

public record AssessmentAnswerResponse(
        String id,
        String questionId,
        String optionId,
        String dimension,
        String value,
        Integer score
) {
}