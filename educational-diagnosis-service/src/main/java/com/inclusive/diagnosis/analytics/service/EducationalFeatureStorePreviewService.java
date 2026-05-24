package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.EducationalFeatureStorePreviewResponse;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import com.inclusive.diagnosis.response.repository.DiagnosticResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EducationalFeatureStorePreviewService {

    private final DiagnosticResponseRepository responseRepository;

    private final InterventionExecutionRepository interventionRepository;

    public EducationalFeatureStorePreviewResponse generatePreview() {

        var responses = responseRepository.findAll();

        var interventions = interventionRepository.findAll();

        long totalResponses = responses.size();

        long reflectiveResponses = responses.stream()
                .filter(item -> "REFLECTIVE_OBSERVATION".equals(
                        item.getResponseValue()
                ))
                .count();

        double kolbReflectiveScore = totalResponses == 0
                ? 0.0
                : reflectiveResponses / (double) totalResponses;

        double averageProgress = interventions.stream()
                .mapToDouble(item -> item.getProgressScore())
                .average()
                .orElse(0.0);

        long improvedEngagementCount = interventions.stream()
                .filter(item -> Boolean.TRUE.equals(
                        item.getEngagementImproved()
                ))
                .count();

        double engagementImprovementRatio = interventions.isEmpty()
                ? 0.0
                : improvedEngagementCount / (double) interventions.size();

        double engagementRiskScore = 1.0 - engagementImprovementRatio;

        double supportIntensityScore = averageProgress < 0.5
                ? 0.90
                : averageProgress < 0.8
                        ? 0.60
                        : 0.25;

        double interventionEffectivenessScore = averageProgress;

        double longitudinalImprovementScore =
                averageProgress * engagementImprovementRatio;

        double adaptiveComplexityScore =
                (engagementRiskScore + supportIntensityScore) / 2.0;

        String studentId = interventions.stream()
                .findFirst()
                .map(item -> item.getStudentProfileId().toString())
                .orElse("NO_INTERVENTION_SAMPLE");

        String policy =
                "Feature store excludes clinical labels and uses only adaptive educational signals such as learning responses, engagement, intervention effectiveness and support intensity.";

        return new EducationalFeatureStorePreviewResponse(
                studentId,
                kolbReflectiveScore,
                engagementRiskScore,
                supportIntensityScore,
                interventionEffectivenessScore,
                longitudinalImprovementScore,
                adaptiveComplexityScore,
                policy
        );
    }
}
