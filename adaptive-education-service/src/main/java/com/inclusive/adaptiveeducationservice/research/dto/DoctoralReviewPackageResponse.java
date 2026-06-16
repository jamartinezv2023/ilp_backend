package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record DoctoralReviewPackageResponse(

        String reviewPackageStatus,

        String advisorReviewReadiness,

        List<String> includedEvidence,

        List<String> pendingAcademicActions,

        String recommendedAdvisorDiscussionFocus,

        String nextDoctoralMilestone
) {
}
