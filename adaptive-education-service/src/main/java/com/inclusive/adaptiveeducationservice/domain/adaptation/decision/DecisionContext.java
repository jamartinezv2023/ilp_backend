package com.inclusive.adaptiveeducationservice.domain.adaptation.decision;
import java.time.OffsetDateTime;

public record DecisionContext(
    Long studentId, String studentName, String tenantId, String assessmentId,
    Double score, String result, OffsetDateTime timestamp, String assessmentType,
    String policyVersion, String extraData
) {
    public Long getStudentId() { return studentId; }
    public String getTenantId() { return tenantId; }
    public String getAssessmentId() { return assessmentId; }
    public String getAssessmentType() { return assessmentType; }
    public Double getScore() { return score; }
    public String getResult() { return result; }
    public String getPolicyVersion() { return policyVersion; }
}