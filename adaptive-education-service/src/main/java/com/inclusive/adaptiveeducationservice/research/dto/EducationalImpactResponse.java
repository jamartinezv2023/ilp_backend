package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record EducationalImpactResponse(

        String impactLevel,

        String teacherSupportImpact,

        String studentInclusionImpact,

        String institutionalDecisionImpact,

        List<String> expectedBenefits,

        String doctoralContributionStatus
) {
}
