package com.inclusive.adaptiveeducationservice.domain.adaptation.decision;
import java.util.List;

public record PolicyResult(
    String recommendation,
    String rationale,
    List<String> recommendedItems,
    Double confidence,
    String policyName,
    String trace
) {
    public String getRationale() { return rationale; }
    public String getPolicyName() { return policyName; }
    public Double getConfidence() { return confidence; }
    public List<String> getRecommendedItems() { return recommendedItems; }
    public String itemsAsCsv() { return String.join(",", recommendedItems); }
    public String traceAsText() { return trace; }
}