package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.LearningEvolutionResponse;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningEvolutionService {

    private final InterventionExecutionRepository interventionRepository;

    public LearningEvolutionResponse analyze(
            UUID studentProfileId
    ) {

        var interventions = interventionRepository
                .findByStudentProfileId(studentProfileId);

        if (interventions.isEmpty()) {
            return new LearningEvolutionResponse(
                    studentProfileId.toString(),
                    "NO_DATA",
                    0.0,
                    0,
                    0,
                    "UNKNOWN",
                    "Register at least one pedagogical intervention outcome."
            );
        }

        double averageProgress = interventions.stream()
                .mapToDouble(item -> item.getProgressScore())
                .average()
                .orElse(0.0);

        long improvedCount = interventions.stream()
                .filter(item -> Boolean.TRUE.equals(
                        item.getEngagementImproved()
                ))
                .count();

        String evolutionTrend = averageProgress >= 0.8
                ? "POSITIVE"
                : averageProgress >= 0.5
                        ? "STABLE"
                        : "AT_RISK";

        String engagementTrend = improvedCount >=
                Math.ceil(interventions.size() / 2.0)
                ? "IMPROVING"
                : "NEEDS_ATTENTION";

        String nextPhase = "POSITIVE".equals(evolutionTrend)
                ? "Maintain reflective asynchronous strategy and monitor weekly."
                : "Adjust intervention intensity and collect additional evidence.";

        return new LearningEvolutionResponse(
                studentProfileId.toString(),
                evolutionTrend,
                averageProgress,
                interventions.size(),
                improvedCount,
                engagementTrend,
                nextPhase
        );
    }
}
