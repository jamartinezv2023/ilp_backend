package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.InterventionOrchestrationPreviewResponse;
import com.inclusive.diagnosis.intervention.entity.InterventionExecution;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterventionOrchestrationPreviewService {

    private final InterventionExecutionRepository interventionRepository;

    public InterventionOrchestrationPreviewResponse generatePreview() {

        var interventions = interventionRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                InterventionExecution::getExecutedAt
                        )
                )
                .toList();

        if (interventions.isEmpty()) {
            return new InterventionOrchestrationPreviewResponse(
                    "NO_ORCHESTRATION_SAMPLE",
                    "EVIDENCE_COLLECTION",
                    List.of(
                            "Collect classroom evidence",
                            "Register intervention outcomes",
                            "Generate support forecast",
                            "Build adaptive recommendation sequence"
                    ),
                    "Start orchestration after at least two intervention records.",
                    0.0,
                    "Orchestration excludes clinical treatment and only sequences inclusive educational support pathways."
            );
        }

        var first = interventions.get(0);
        var last = interventions.get(interventions.size() - 1);

        double delta = last.getProgressScore() - first.getProgressScore();

        boolean engagementImproved = Boolean.TRUE.equals(
                last.getEngagementImproved()
        );

        String phase;

        List<String> order;

        String trigger;

        double confidence;

        if (last.getProgressScore() < 0.5 && !engagementImproved) {
            phase = "ESCALATION";

            order = List.of(
                    "Immediate structured reflective support",
                    "PIAR-oriented reasonable adjustment review",
                    "Executive function scaffolding",
                    "Institutional support team follow-up"
            );

            trigger =
                    "Escalate support if progress remains below 0.50 or engagement does not improve.";

            confidence = 0.86;

        } else if (delta > 0.10 || engagementImproved) {
            phase = "STABILIZATION";

            order = List.of(
                    "Maintain reflective support",
                    "Introduce collaborative multimodal activity",
                    "Executive function scaffolding",
                    "Progressive autonomy reinforcement"
            );

            trigger =
                    "Escalate support if engagement drops or progress falls below 0.50.";

            confidence = 0.87;

        } else {
            phase = "MONITORING";

            order = List.of(
                    "Continue differentiated instruction",
                    "Monitor engagement weekly",
                    "Adjust pacing based on classroom evidence",
                    "Reassess support dimension profile"
            );

            trigger =
                    "Increase intensity if no progress is observed after the next monitoring cycle.";

            confidence = 0.72;
        }

        return new InterventionOrchestrationPreviewResponse(
                last.getStudentProfileId().toString(),
                phase,
                order,
                trigger,
                confidence,
                "Orchestration engine excludes clinical labels and coordinates only adaptive educational support pathways."
        );
    }
}
