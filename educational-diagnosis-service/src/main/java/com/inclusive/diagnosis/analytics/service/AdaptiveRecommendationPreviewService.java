package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.AdaptiveRecommendationPreviewResponse;
import com.inclusive.diagnosis.intervention.entity.InterventionExecution;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdaptiveRecommendationPreviewService {

    private final InterventionExecutionRepository interventionRepository;

    public AdaptiveRecommendationPreviewResponse generatePreview() {

        var interventions = interventionRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                InterventionExecution::getExecutedAt
                        )
                )
                .toList();

        if (interventions.isEmpty()) {
            return new AdaptiveRecommendationPreviewResponse(
                    "NO_RECOMMENDATION_SAMPLE",
                    List.of(
                            "Collect intervention evidence",
                            "Register engagement observations",
                            "Generate temporal support sequence"
                    ),
                    "INSUFFICIENT_EVIDENCE",
                    0.0,
                    "Recommendations exclude clinical advice and only sequence educational support actions."
            );
        }

        var last = interventions.get(interventions.size() - 1);

        double progressScore = last.getProgressScore();

        boolean engagementImproved = Boolean.TRUE.equals(
                last.getEngagementImproved()
        );

        List<String> sequence;

        String outcome;

        double confidence;

        if (progressScore < 0.5 && !engagementImproved) {
            sequence = List.of(
                    "Escalate structured reflective support",
                    "Activate PIAR-oriented reasonable adjustments",
                    "Introduce multimodal guided instruction",
                    "Schedule weekly monitoring with institutional support team"
            );
            outcome = "SUPPORT_ESCALATION_REQUIRED";
            confidence = 0.86;
        } else if (progressScore < 0.8) {
            sequence = List.of(
                    "Maintain reflective guided journal",
                    "Introduce multimodal collaborative activity",
                    "Monitor engagement weekly",
                    "Adjust pacing based on classroom evidence"
            );
            outcome = "STABILIZING_ENGAGEMENT";
            confidence = 0.84;
        } else {
            sequence = List.of(
                    "Maintain current inclusive strategy",
                    "Reduce support intensity gradually",
                    "Document sustained progress evidence",
                    "Promote autonomous learning activities"
            );
            outcome = "SUSTAINED_PROGRESS_EXPECTED";
            confidence = 0.88;
        }

        return new AdaptiveRecommendationPreviewResponse(
                last.getStudentProfileId().toString(),
                sequence,
                outcome,
                confidence,
                "Recommendation engine excludes clinical labels and recommends only adaptive educational support sequences."
        );
    }
}
