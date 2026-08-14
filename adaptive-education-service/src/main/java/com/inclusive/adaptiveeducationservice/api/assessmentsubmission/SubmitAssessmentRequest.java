package com.inclusive.adaptiveeducationservice.api.assessmentsubmission;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SubmitAssessmentRequest(

        @NotBlank
        String administrationId,

        @NotBlank
        String participantId,
        @NotNull UUID researchParticipantUuid,

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