package com.inclusive.adaptiveeducationservice.api.assessmentsubmission;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record SubmitAssessmentRequest(

        @NotBlank
        String administrationId,

        @NotBlank
        String participantId,

        @NotBlank
        String assessmentCode,

        @NotBlank
        String assessmentVersion,

        @NotEmpty
        List<@Valid SubmitAssessmentQuestionRequest> responses,

        Map<String, String> context,

        Instant submittedAt
) {

    public SubmitAssessmentRequest {
        responses = responses == null
                ? List.of()
                : List.copyOf(responses);

        context = context == null
                ? Map.of()
                : Map.copyOf(context);

        submittedAt = submittedAt == null
                ? Instant.now()
                : submittedAt;
    }
}