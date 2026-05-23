package com.inclusive.diagnosis.analytics.dto;

import java.util.List;

public record EvidenceTraceabilityResponse(

        String studentProfileId,

        List<String> responseEvidence,

        List<String> indicatorEvidence,

        List<String> diagnosisEvidence,

        List<String> recommendationEvidence,

        List<String> traceabilitySummary
) {
}
