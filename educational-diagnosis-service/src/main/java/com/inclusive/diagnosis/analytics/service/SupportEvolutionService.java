package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.SupportEvolutionResponse;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportEvolutionService {

    private final InterventionExecutionRepository interventionRepository;

    public SupportEvolutionResponse analyze(
            UUID studentProfileId
    ) {

        var interventions = interventionRepository.findByStudentProfileId(
                studentProfileId
        );

        if (interventions.isEmpty()) {
            return new SupportEvolutionResponse(
                    studentProfileId.toString(),
                    "UNDETERMINED",
                    "NO_DATA",
                    "NO_LONGITUDINAL_EVIDENCE",
                    "Register intervention outcomes before evaluating support evolution.",
                    List.of("No intervention records available.")
            );
        }

        double averageProgress = interventions.stream()
                .mapToDouble(item -> item.getProgressScore())
                .average()
                .orElse(0.0);

        long improvedEngagement = interventions.stream()
                .filter(item -> Boolean.TRUE.equals(
                        item.getEngagementImproved()
                ))
                .count();

        String trend;

        String evolution;

        String action;

        if (averageProgress >= 0.8 && improvedEngagement > 0) {
            trend = "IMPROVING";
            evolution = "POSITIVE_RESPONSE_TO_SUPPORT";
            action = "Maintain current support strategy and reduce intensity only after sustained evidence.";
        } else if (averageProgress >= 0.5) {
            trend = "PERSISTENT";
            evolution = "PARTIAL_IMPROVEMENT";
            action = "Continue support with additional scaffolding and weekly monitoring.";
        } else {
            trend = "ESCALATION_REQUIRED";
            evolution = "NO_SIGNIFICANT_IMPROVEMENT";
            action = "Increase structured support, review PIAR adjustments and involve institutional support team.";
        }

        List<String> evidence = interventions.stream()
                .map(item -> "progress="
                        + item.getProgressScore()
                        + " | engagementImproved="
                        + item.getEngagementImproved()
                        + " | observations="
                        + item.getTeacherObservations())
                .toList();

        return new SupportEvolutionResponse(
                studentProfileId.toString(),
                "ATTENTION_EXECUTIVE_SUPPORT",
                trend,
                evolution,
                action,
                evidence
        );
    }
}
