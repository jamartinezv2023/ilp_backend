package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.InstitutionalPolicyRecommendationResponse;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import com.inclusive.diagnosis.response.repository.DiagnosticResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionalPolicyRecommendationService {

    private final DiagnosticResponseRepository responseRepository;

    private final InterventionExecutionRepository interventionRepository;

    public InstitutionalPolicyRecommendationResponse generate() {

        var responses = responseRepository.findAll();
        var interventions = interventionRepository.findAll();

        long totalResponses = responses.size();

        long reflectiveResponses = responses.stream()
                .filter(response -> "REFLECTIVE_OBSERVATION".equals(
                        response.getResponseValue()
                ))
                .count();

        double reflectiveRatio = totalResponses == 0
                ? 0.0
                : reflectiveResponses / (double) totalResponses;

        double averageProgress = interventions.stream()
                .mapToDouble(item -> item.getProgressScore())
                .average()
                .orElse(0.0);

        double riskPopulationPercentage = averageProgress < 0.5
                ? 0.60
                : averageProgress < 0.8
                        ? 0.34
                        : 0.12;

        String trend = reflectiveRatio >= 0.5
                ? "High reflective learning prevalence"
                : "Mixed learning profile distribution";

        String supportArea = averageProgress < 0.8
                ? "Student engagement"
                : "Sustained inclusive learning support";

        String action = reflectiveRatio >= 0.5
                ? "Increase asynchronous reflective learning resources and guided metacognitive activities."
                : "Diversify instructional strategies across reflective, active, abstract and concrete learning paths.";

        List<String> patterns = List.of(
                "Reflective response ratio: " + reflectiveRatio,
                "Average intervention progress: " + averageProgress,
                "Estimated risk population: " + riskPopulationPercentage
        );

        return new InstitutionalPolicyRecommendationResponse(
                trend,
                action,
                riskPopulationPercentage,
                supportArea,
                patterns
        );
    }
}
