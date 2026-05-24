package com.inclusive.diagnosis.intervention.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateInterventionExecutionRequest(

        @NotNull
        UUID tenantId,

        @NotNull
        UUID studentProfileId,

        @NotBlank
        String interventionCategory,

        @NotBlank
        String interventionDescription,

        @NotBlank
        String responsibleTeacher,

        @NotNull
        Boolean engagementImproved,

        @NotNull
        Double progressScore,

        String teacherObservations
) {
}
