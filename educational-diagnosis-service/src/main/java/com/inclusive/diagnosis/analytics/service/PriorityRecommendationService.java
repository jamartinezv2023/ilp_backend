package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.PedagogicalPriorityPlanResponse;
import com.inclusive.diagnosis.diagnosis.entity.InclusiveDiagnosis;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
                .max(Comparator.comparing(
                        InclusiveDiagnosis::getConfidenceScore
                ))
                .orElse(null);

        if (mainDiagnosis == null) {
            return new PedagogicalPriorityPlanResponse(
                    "LOW",
                    "Collect more diagnostic evidence",
                    "Ask the student to complete diagnostic questionnaires before assigning interventions.",
                    "No diagnosis available for this student.",
                    0.0
            );
        }

        boolean hasBarrier = mainDiagnosis.getIdentifiedBarriers() != null
                && !mainDiagnosis.getIdentifiedBarriers().isBlank();

        boolean highConfidence =
                mainDiagnosis.getConfidenceScore() != null
                        && mainDiagnosis.getConfidenceScore() >= 0.9;

        String priorityLevel = highConfidence && hasBarrier
                ? "HIGH"
                : "MEDIUM";

        String action = "Prioritize reflective learning support";

        String teacherRecommendation = highConfidence && hasBarrier
                ? "Start with structured reflective activities and monitor participation barriers."
                : "Use reflective activities and collect additional evidence to refine the diagnosis.";

        String evidence = "Diagnosis: "
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
                mainDiagnosis.getConfidenceScore()
        );
    }
}
