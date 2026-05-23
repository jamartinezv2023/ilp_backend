package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.PedagogicalPriorityPlanResponse;
import com.inclusive.diagnosis.diagnosis.entity.InclusiveDiagnosis;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriorityRecommendationService {

    private final InclusiveDiagnosisRepository diagnosisRepository;

    public PedagogicalPriorityPlanResponse generatePriorityPlan(
            UUID studentProfileId
    ) {

        var diagnoses = diagnosisRepository.findByStudentProfileId(
                studentProfileId
        );

        var mainDiagnosis = diagnoses.stream()
                .max(
                        Comparator.comparing(
                                InclusiveDiagnosis::getConfidenceScore
                        )
                )
                .orElse(null);

        if (mainDiagnosis == null) {

            return new PedagogicalPriorityPlanResponse(
                    "LOW",
                    "Collect more diagnostic evidence",
                    "Ask the student to complete diagnostic questionnaires before assigning interventions.",
                    "No diagnosis available for this student.",
                    0.0,
                    List.of(
                            "No educational evidence available",
                            "No diagnosis generated",
                            "Additional responses required"
                    )
            );
        }

        List<String> reasoning = new ArrayList<>();

        boolean hasBarrier =
                mainDiagnosis.getIdentifiedBarriers() != null
                        && !mainDiagnosis.getIdentifiedBarriers()
                        .isBlank();

        boolean highConfidence =
                mainDiagnosis.getConfidenceScore() != null
                        && mainDiagnosis.getConfidenceScore() >= 0.9;

        if (highConfidence) {
            reasoning.add(
                    "High confidence score detected"
            );
        }

        if (hasBarrier) {
            reasoning.add(
                    "Learning barriers identified"
            );
        }

        if (mainDiagnosis.getLearningStrengths() != null
                && !mainDiagnosis.getLearningStrengths().isBlank()) {

            reasoning.add(
                    "Strong learning strengths detected"
            );
        }

        reasoning.add(
                "Reflective learning dominance inferred from aggregated responses"
        );

        String priorityLevel =
                highConfidence && hasBarrier
                        ? "HIGH"
                        : "MEDIUM";

        String action =
                "Prioritize reflective learning support";

        String teacherRecommendation =
                highConfidence && hasBarrier
                        ? "Start with structured reflective activities and monitor participation barriers."
                        : "Use reflective activities and collect additional evidence to refine the diagnosis.";

        String evidence =
                "Diagnosis: "
                        + mainDiagnosis.getDiagnosisCategory()
                        + " | Barriers: "
                        + mainDiagnosis.getIdentifiedBarriers()
                        + " | Strengths: "
                        + mainDiagnosis.getLearningStrengths();

        return new PedagogicalPriorityPlanResponse(
                priorityLevel,
                action,
                teacherRecommendation,
                evidence,
                mainDiagnosis.getConfidenceScore(),
                reasoning
        );
    }
}
