package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.EducationalMlDatasetPreviewResponse;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import com.inclusive.diagnosis.response.repository.DiagnosticResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EducationalMlDatasetPreviewService {

    private final DiagnosticResponseRepository responseRepository;

    private final InterventionExecutionRepository interventionRepository;

    public EducationalMlDatasetPreviewResponse generatePreview() {

        var responses = responseRepository.findAll();

        var interventions = interventionRepository.findAll();

        long reflectiveCount = responses.stream()
                .filter(item -> "REFLECTIVE_OBSERVATION".equals(
                        item.getResponseValue()
                ))
                .count();

        long activeCount = responses.stream()
                .filter(item -> "ACTIVE_EXPERIMENTATION".equals(
                        item.getResponseValue()
                ))
                .count();

        String kolbStyle = reflectiveCount >= activeCount
                ? "REFLECTIVE"
                : "ACTIVE";

        var intervention = interventions.stream()
                .findFirst()
                .orElse(null);

        double progressScore = intervention == null
                ? 0.0
                : intervention.getProgressScore();

        boolean engagementImproved = intervention != null
                && Boolean.TRUE.equals(
                        intervention.getEngagementImproved()
                );

        String supportDimension = progressScore < 0.6
                ? "ATTENTION_EXECUTIVE_SUPPORT"
                : "UNIVERSAL_CLASSROOM_SUPPORT";

        String riskLevel;

        if (progressScore < 0.5) {
            riskLevel = "HIGH";
        } else if (progressScore < 0.8) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }

        String supportIntensity = "HIGH".equals(riskLevel)
                ? "ESCALATED"
                : "MEDIUM".equals(riskLevel)
                        ? "MONITORED"
                        : "MAINTAINED";

        String studentId = intervention == null
                ? "NO_INTERVENTION_SAMPLE"
                : intervention.getStudentProfileId().toString();

        String ethicalPolicy =
                "Dataset excludes clinical labels such as disability, disorder, ADHD, autism or dyslexia. It uses educational support dimensions, intervention progress and engagement signals.";

        return new EducationalMlDatasetPreviewResponse(
                studentId,
                kolbStyle,
                supportDimension,
                progressScore,
                engagementImproved,
                riskLevel,
                supportIntensity,
                ethicalPolicy
        );
    }
}
