package com.inclusive.diagnosis.student.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public record CreateStudentProfileRequest(

        UUID tenantId,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String studentCode,

        LocalDate birthDate,

        String gradeLevel,

        String institutionName,

        Boolean hasDisabilitySupportNeeds,

        String accessibilityNotes,

        String preferredLearningStyle,

        String communicationPreferences
) {
}
