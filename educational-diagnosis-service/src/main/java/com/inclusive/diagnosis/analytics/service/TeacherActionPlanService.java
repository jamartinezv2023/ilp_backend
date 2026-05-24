package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.TeacherActionPlanResponse;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import com.inclusive.diagnosis.dua.repository.DuaRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherActionPlanService {

    private final InclusiveDiagnosisRepository diagnosisRepository;

    private final DuaRecommendationRepository recommendationRepository;

    public TeacherActionPlanResponse generate(
            UUID studentProfileId
    ) {

        var diagnosis = diagnosisRepository
                .findByStudentProfileId(studentProfileId)
                .stream()
                .findFirst()
                .orElse(null);

        var recommendation = recommendationRepository
                .findByStudentProfileId(studentProfileId)
                .stream()
                .findFirst()
                .orElse(null);

        if (diagnosis == null || recommendation == null) {
            return new TeacherActionPlanResponse(
                    "LOW",
                    "Collect sufficient diagnostic evidence",
                    "Ask the student to complete the diagnostic process.",
                    "PENDING",
                    "Re-run the diagnostic flow after collecting responses.",
                    List.of(
                            "No complete diagnosis/recommendation chain available."
                    ),
                    List.of(
                            "At least one response, indicator, diagnosis and DUA recommendation must exist."
                    )
            );
        }

        return new TeacherActionPlanResponse(
                "HIGH",
                "Strengthen reflective learning participation",
                recommendation.getRecommendationText(),
                recommendation.getDuaPrinciple(),
                "Review student progress after one week of guided reflective activities.",
                List.of(
                        diagnosis.getDiagnosisSummary(),
                        diagnosis.getIdentifiedBarriers(),
                        diagnosis.getLearningStrengths()
                ),
                List.of(
                        "Student completes reflective activity.",
                        "Student participates at least once asynchronously.",
                        "Teacher records evidence of engagement."
                )
        );
    }
}
