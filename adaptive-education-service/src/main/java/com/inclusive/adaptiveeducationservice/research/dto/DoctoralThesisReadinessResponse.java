package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DoctoralThesisReadinessResponse(

        String thesisReadinessLevel,

        String methodologicalReadiness,

        String technicalReadiness,

        String ethicalReadiness,

        List<String> readyThesisSections,

        String recommendedAcademicAction
) {
}
