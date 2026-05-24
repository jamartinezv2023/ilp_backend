package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.EarlySupportAlertResponse;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import com.inclusive.diagnosis.intervention.repository.InterventionExecutionRepository;
import com.inclusive.diagnosis.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EarlySupportAlertService {

    private final StudentProfileRepository studentRepository;

    private final InclusiveDiagnosisRepository diagnosisRepository;

    private final InterventionExecutionRepository interventionRepository;

    public EarlySupportAlertResponse analyze(
            UUID studentProfileId
    ) {

        var student = studentRepository.findById(studentProfileId)
                .orElse(null);

        var diagnoses = diagnosisRepository.findByStudentProfileId(
                studentProfileId
        );

        var interventions = interventionRepository.findByStudentProfileId(
                studentProfileId
        );

        List<String> signals = new ArrayList<>();

        double score = 0.0;

        if (student != null
                && Boolean.TRUE.equals(
                        student.getHasDisabilitySupportNeeds()
                )) {

            score += 0.35;
            signals.add(
                    "Student profile reports possible accessibility support needs"
            );
        }

        boolean barriersDetected = diagnoses.stream()
                .anyMatch(item -> item.getIdentifiedBarriers() != null
                        && !item.getIdentifiedBarriers().isBlank());

        if (barriersDetected) {
            score += 0.30;
            signals.add(
                    "Learning barriers detected in inclusive diagnosis"
            );
        }

        double averageProgress = interventions.stream()
                .mapToDouble(item -> item.getProgressScore())
                .average()
                .orElse(0.0);

        if (!interventions.isEmpty() && averageProgress < 0.6) {
            score += 0.25;
            signals.add(
                    "Low progress after pedagogical intervention"
            );
        }

        long improvedEngagement = interventions.stream()
                .filter(item -> Boolean.TRUE.equals(
                        item.getEngagementImproved()
                ))
                .count();

        if (!interventions.isEmpty() && improvedEngagement == 0) {
            score += 0.20;
            signals.add(
                    "No engagement improvement observed"
            );
        }

        String alertLevel;

        if (score >= 0.75) {
            alertLevel = "HIGH_SUPPORT_ALERT";
        } else if (score >= 0.45) {
            alertLevel = "MODERATE_SUPPORT_ALERT";
        } else {
            alertLevel = "LOW_SUPPORT_ALERT";
        }

        String alertType =
                "EARLY_PEDAGOGICAL_SUPPORT_NEED";

        String ethicalWarning =
                "This alert is not a medical diagnosis. It only indicates educational support needs based on observed pedagogical evidence.";

        String teacherAction =
                "Document observations, apply inclusive strategies, communicate respectfully with family or support team, and request professional evaluation when institutionally appropriate.";

        String piarRecommendation = score >= 0.45
                ? "Consider initiating or updating a PIAR-oriented reasonable adjustment plan focused on accessibility, flexible pacing, alternative evidence of learning and structured follow-up."
                : "Continue universal classroom supports and collect additional pedagogical evidence before escalating.";

        return new EarlySupportAlertResponse(
                studentProfileId.toString(),
                alertLevel,
                alertType,
                score,
                signals,
                ethicalWarning,
                teacherAction,
                piarRecommendation
        );
    }
}
