package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EducationalAdoptionReadinessResponse(

        String adoptionReadinessLevel,

        String teacherPreparedness,

        String institutionalPreparedness,

        String ethicalDeploymentReadiness,

        List<String> adoptionRequirements,

        String recommendedAdoptionPhase
) {
}
