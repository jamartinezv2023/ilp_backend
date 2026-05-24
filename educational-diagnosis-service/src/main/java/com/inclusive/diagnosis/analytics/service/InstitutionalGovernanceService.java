package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.InstitutionalGovernanceReportResponse;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import com.inclusive.diagnosis.response.repository.DiagnosticResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionalGovernanceService {

    private final DiagnosticResponseRepository responseRepository;

    private final InterventionExecutionRepository interventionRepository;

    public InstitutionalGovernanceReportResponse generate() {

        var responses = responseRepository.findAll();

        var interventions = interventionRepository.findAll();

        List<String> reasoning = new ArrayList<>();

        double riskScore = 0.0;

        if (responses.isEmpty()) {
            riskScore += 0.4;

            reasoning.add(
                    "Low institutional diagnostic evidence detected"
            );
        }

        long reflectiveResponses = responses.stream()
                .filter(response ->
                        "REFLECTIVE_OBSERVATION".equals(
                                response.getResponseValue()
                        )
                )
                .count();

        if (!responses.isEmpty()) {

            double reflectiveRatio =
                    reflectiveResponses / (double) responses.size();

            if (reflectiveRatio >= 0.5) {

                reasoning.add(
                        "Reflective learning prevalence detected"
                );
            }
        }

        double averageProgress = interventions.stream()
                .mapToDouble(item ->
                        item.getProgressScore()
                )
                .average()
                .orElse(0.0);

        if (averageProgress < 0.5) {

            riskScore += 0.4;

            reasoning.add(
                    "Low intervention effectiveness detected"
            );

        } else if (averageProgress < 0.8) {

            riskScore += 0.2;

            reasoning.add(
                    "Moderate intervention effectiveness detected"
            );

        } else {

            reasoning.add(
                    "Positive intervention evolution detected"
            );
        }

        String status;

        String priority;

        String action;

        if (riskScore >= 0.7) {

            status = "CRITICAL";

            priority = "HIGH";

            action =
                    "Increase institutional pedagogical support and monitoring immediately.";

        } else if (riskScore >= 0.4) {

            status = "AT_RISK";

            priority = "MEDIUM";

            action =
                    "Strengthen adaptive inclusive intervention coverage.";

        } else {

            status = "STABLE";

            priority = "LOW";

            action =
                    "Maintain current adaptive institutional strategy and continue evidence collection.";
        }

        return new InstitutionalGovernanceReportResponse(
                status,
                reasoning,
                action,
                priority,
                riskScore
        );
    }
}
