package com.inclusive.adaptiveeducationservice.research.dto;

import java.util.List;

public record ArchitectureEvaluationMatrixResponse(

        String evaluationMatrixStatus,

        String rubricAlignmentLevel,

        List<String> evaluatedCriteria,

        List<String> currentEvidence,

        List<String> pendingEvidence,

        String recommendedEvaluatorAction
) {
}
