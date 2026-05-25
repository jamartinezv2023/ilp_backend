package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.SupportForecastPreviewResponse;
import com.inclusive.diagnosis.intervention.entity.InterventionExecution;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class SupportForecastPreviewService {

    private final InterventionExecutionRepository interventionRepository;

    public SupportForecastPreviewResponse generatePreview() {

        var interventions = interventionRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                InterventionExecution::getExecutedAt
                        )
                )
                .toList();

        if (interventions.isEmpty()) {
            return new SupportForecastPreviewResponse(
                    "NO_FORECAST_SAMPLE",
                    "UNKNOWN",
                    "ESCALATED",
                    0.0,
                    0.0,
                    "Collect at least two intervention records before forecasting support evolution.",
                    "Forecast excludes clinical prediction and only estimates educational support trajectory."
            );
        }

        var first = interventions.get(0);
        var last = interventions.get(interventions.size() - 1);

        double delta = last.getProgressScore() - first.getProgressScore();

        String engagementTrend = Boolean.TRUE.equals(
                last.getEngagementImproved()
        )
                ? "IMPROVING"
                : delta > 0.10
                        ? "PARTIALLY_IMPROVING"
                        : "NEEDS_ATTENTION";

        String supportIntensity;

        if (last.getProgressScore() < 0.5) {
            supportIntensity = "HIGH";
        } else if (last.getProgressScore() < 0.8) {
            supportIntensity = "MODERATE";
        } else {
            supportIntensity = "LOW";
        }

        double predictedEffectiveness =
                Math.min(1.0, Math.max(0.0, last.getProgressScore() + delta));

        double confidence = interventions.size() >= 2
                ? 0.82
                : 0.50;

        String nextPhase = switch (supportIntensity) {
            case "HIGH" ->
                    "Escalate structured support and review PIAR adjustments with the institutional team.";
            case "MODERATE" ->
                    "Maintain structured reflective intervention and monitor weekly.";
            default ->
                    "Gradually maintain support while documenting sustained progress.";
        };

        return new SupportForecastPreviewResponse(
                last.getStudentProfileId().toString(),
                engagementTrend,
                supportIntensity,
                predictedEffectiveness,
                confidence,
                nextPhase,
                "Forecast excludes clinical labels and models only adaptive educational support evolution."
        );
    }
}
