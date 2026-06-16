package com.inclusive.adaptiveeducationservice.student.dto;

import java.util.List;

public record StudentProfileResponse(

        String id,

        String fullName,

        String grade,

        Integer age,

        String learningProfile,

        String vocationalInterest,

        String supportLevel,

        List<String> inclusiveStrategies,

        List<String> pedagogicalRecommendations
) {
}
