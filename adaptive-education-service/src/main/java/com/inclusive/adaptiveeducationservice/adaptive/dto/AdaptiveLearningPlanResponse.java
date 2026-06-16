package com.inclusive.adaptiveeducationservice.adaptive.dto;

import java.time.Instant;
import java.util.List;

public record AdaptiveLearningPlanResponse(

        String planId,

        String studentId,

        String fullName,

        String learningProfile,

        List<String> learningPreferences,

        String vocationalInterest,

        String supportLevel,

        String riskLevel,

        String recommendedMethodology,

        List<String> recommendedResources,

        List<String> adaptivePathway,

        List<String> teacherActions,

        List<String> inclusionActions,

        List<String> familyActions,

        Instant createdAt
) {
}