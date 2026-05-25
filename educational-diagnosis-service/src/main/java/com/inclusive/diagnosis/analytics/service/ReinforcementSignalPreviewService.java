package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.ReinforcementSignalPreviewResponse;
import com.inclusive.diagnosis.intervention.entity.InterventionExecution;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class ReinforcementSignalPreviewService {

    private final InterventionExecutionRepository interventionRepository;

    public ReinforcementSignalPreviewResponse generatePreview() {

        var interventions = interventionRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                InterventionExecution::getExecutedAt
                        )
                )
                .toList();

        if (interventions.isEmpty()) {
            return new ReinforcementSignalPreviewResponse(
                    "NO_REINFORCEMENT_SAMPLE",
                    0.0,
                    "No intervention evidence available.",
                    "Collect longitudinal intervention evidence before calculating reward signals.",
                    "No adaptive policy feedback can be inferred yet.",
                    "Reward modeling excludes clinical labels and only evaluates inclusive educational progress signals."
            );
        }

        var first = interventions.get(0);

        var last = interventions.get(
                interventions.size() - 1
        );

        double progressDelta =
                last.getProgressScore()
                        - first.getProgressScore();

        boolean engagementImproved =
                Boolean.TRUE.equals(
                        last.getEngagementImproved()
                );

        double rewardSignal =
                Math.min(
                        1.0,
                        Math.max(
                                0.0,
                                0.50
                                        + progressDelta
                                        + (engagementImproved ? 0.25 : -0.10)
                        )
                );

        String reason;

        String nextAction;

        String feedback;

        if (rewardSignal >= 0.75) {
            reason =
                    "Engagement and progress improved after adaptive educational support.";

            nextAction =
                    "Continue stabilization phase with progressive autonomy.";

            feedback =
                    "Current adaptive support sequence produced positive educational reinforcement.";

        } else if (rewardSignal >= 0.50) {
            reason =
                    "Partial improvement detected in the educational support trajectory.";

            nextAction =
                    "Maintain support and increase monitoring frequency.";

            feedback =
                    "Current policy produced moderate educational reinforcement.";

        } else {
            reason =
                    "Limited progress or engagement improvement detected.";

            nextAction =
                    "Escalate support intensity and review PIAR-oriented adjustments.";

            feedback =
                    "Current policy requires adaptive refinement.";
        }

        return new ReinforcementSignalPreviewResponse(
                last.getStudentProfileId().toString(),
                rewardSignal,
                reason,
                nextAction,
                feedback,
                "Reward signal excludes medical or clinical targets and only rewards educational support progress, engagement recovery and intervention effectiveness."
        );
    }
}
