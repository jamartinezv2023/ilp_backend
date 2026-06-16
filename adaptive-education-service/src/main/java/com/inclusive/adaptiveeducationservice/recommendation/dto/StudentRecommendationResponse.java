package com.inclusive.adaptiveeducationservice.recommendation.dto;

import java.util.List;

public record StudentRecommendationResponse(

        String studentId,

        String fullName,

        String learningProfile,

        String vocationalInterest,

        String supportLevel,

        List<String> teacherRecommendations,

        List<String> inclusionRecommendations,

        List<String> familyRecommendations,

        List<String> nextActions
) {
}