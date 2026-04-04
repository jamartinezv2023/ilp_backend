package com.inclusive.adaptiveeducationservice.domain.adaptation.log;
public record DecisionLog(
    Long studentId, String tenantId, String assessmentId, String assessmentType,
    Double score, String result, String policyName, String recommendedItems,
    String rationale, Double confidence, String trace
) {}