package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.EducationalRiskProfileResponse;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EducationalRiskService {

    private final InclusiveDiagnosisRepository diagnosisRepository;

    private final InterventionExecutionRepository interventionRepository;

    public EducationalRiskProfileResponse analyzeRisk(
            UUID studentProfileId
    ) {

        var diagnoses = diagnosisRepository.findByStudentProfileId(
                studentProfileId
        );

        var interventions = interventionRepository.findByStudentProfileId(
                studentProfileId
        );

        List<String> factors = new ArrayList<>();

        double riskScore = 0.0;

        boolean hasBarriers = diagnoses.stream()
                .anyMatch(item -> item.getIdentifiedBarriers() != null
                        && !item.getIdentifiedBarriers().isBlank());

        if (hasBarriers) {
            riskScore += 0.35;
            factors.add("Learning barriers detected");
        }

        double averageProgress = interventions.stream()
                .mapToDouble(item -> item.getProgressScore())
                .average()
                .orElse(0.0);

        if (interventions.isEmpty()) {
            riskScore += 0.30;
            factors.add("No intervention evidence available");
        } else if (averageProgress < 0.5) {
            riskScore += 0.35;
            factors.add("Low intervention progress detected");
        } else if (averageProgress < 0.8) {
            riskScore += 0.20;
            factors.add("Moderate intervention progress detected");
        }

        long improvedEngagement = interventions.stream()
                .filter(item -> Boolean.TRUE.equals(
                        item.getEngagementImproved()
                ))
                .count();

        if (!interventions.isEmpty()
                && improvedEngagement == 0) {
            riskScore += 0.25;
            factors.add("No engagement improvement detected");
        }

        String riskLevel;

        if (riskScore >= 0.75) {
            riskLevel = "CRITICAL_SUPPORT_REQUIRED";
        } else if (riskScore >= 0.50) {
            riskLevel = "AT_RISK";
        } else if (riskScore >= 0.25) {
            riskLevel = "STABLE_WITH_MONITORING";
        } else {
            riskLevel = "IMPROVING";
        }

        String action = switch (riskLevel) {
            case "CRITICAL_SUPPORT_REQUIRED" ->
                    "Increase intervention intensity and schedule immediate pedagogical follow-up.";
            case "AT_RISK" ->
                    "Increase monitoring frequency and combine reflective activities with guided teacher support.";
            case "STABLE_WITH_MONITORING" ->
                    "Maintain current intervention and review progress weekly.";
            default ->
                    "Continue current strategy and document positive progress evidence.";
        };

        return new EducationalRiskProfileResponse(
                studentProfileId.toString(),
                riskLevel,
                riskScore,
                factors,
                action
        );
    }
}
