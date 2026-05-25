package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.PolicyLearningPreviewResponse;
import com.inclusive.diagnosis.intervention.entity.InterventionExecution;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class PolicyLearningPreviewService {

    private final InterventionExecutionRepository interventionRepository;

    public PolicyLearningPreviewResponse generatePreview() {

        var interventions = interventionRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                InterventionExecution::getExecutedAt
                        )
                )
                .toList();

        if (interventions.isEmpty()) {

            return new PolicyLearningPreviewResponse(
                    "NO_POLICY_SAMPLE",
                    "EVIDENCE_COLLECTION_POLICY",
                    0.0,
                    "Collect intervention evidence before adaptive policy learning.",
                    "No longitudinal intervention evidence available.",
                    0.0,
                    "Policy learning excludes clinical labels and only learns adaptive educational support effectiveness."
            );
        }

        var first = interventions.get(0);

        var last = interventions.get(
                interventions.size() - 1
        );

        double performanceDelta =
                last.getProgressScore()
                        - first.getProgressScore();

        boolean engagementImproved =
                Boolean.TRUE.equals(
                        last.getEngagementImproved()
                );

        String currentPolicy =
                "REFLECTIVE_SUPPORT_SEQUENCE";

        double performanceScore =
                Math.min(
                        1.0,
                        Math.max(
                                0.0,
                                last.getProgressScore()
                                        + (performanceDelta * 0.5)
                        )
                );

        String adjustment;

        String learningSignal;

        double confidence;

        if (engagementImproved && performanceDelta > 0.10) {

            adjustment =
                    "Increase collaborative multimodal reinforcement and progressive autonomy activities.";

            learningSignal =
                    "Engagement improved after multimodal reflective support sequence.";

            confidence = 0.89;

        } else if (performanceDelta > 0.0) {

            adjustment =
                    "Maintain current support policy and increase monitoring frequency.";

            learningSignal =
                    "Partial educational improvement detected.";

            confidence = 0.76;

        } else {

            adjustment =
                    "Escalate structured support intensity and reassess adaptive sequencing.";

            learningSignal =
                    "Limited improvement observed in current intervention trajectory.";

            confidence = 0.71;
        }

        return new PolicyLearningPreviewResponse(
                last.getStudentProfileId().toString(),
                currentPolicy,
                performanceScore,
                adjustment,
                learningSignal,
                confidence,
                "Policy learning excludes clinical labels and optimizes only inclusive educational support effectiveness."
        );
    }
}
