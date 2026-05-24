package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record ClassroomInclusionProfileResponse(

        String classroomComplexity,

        int studentsAnalyzed,

        int studentsRequiringAdaptiveSupport,

        int studentsRequiringPiar,

        String dominantSupportDimension,

        String recommendedClassroomStrategy,

        List<String> detectedClassroomNeeds
) {
}
