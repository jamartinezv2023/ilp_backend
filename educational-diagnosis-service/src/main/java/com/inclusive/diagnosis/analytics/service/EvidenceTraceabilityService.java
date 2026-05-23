package com.inclusive.diagnosis.analytics.service;

import com.inclusive.diagnosis.analytics.dto.EvidenceTraceabilityResponse;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import com.inclusive.diagnosis.dua.repository.DuaRecommendationRepository;
import com.inclusive.diagnosis.indicator.repository.LearningIndicatorRepository;
import com.inclusive.diagnosis.response.repository.DiagnosticResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvidenceTraceabilityService {

    private final DiagnosticResponseRepository responseRepository;

    private final LearningIndicatorRepository indicatorRepository;

    private final InclusiveDiagnosisRepository diagnosisRepository;

    private final DuaRecommendationRepository recommendationRepository;

    public EvidenceTraceabilityResponse trace(
            UUID studentProfileId
    ) {

        List<String> responses = responseRepository
                .findByStudentProfileId(studentProfileId)
                .stream()
                .map(response -> response.getQuestionCode()
                        + " = "
                        + response.getResponseValue())
                .toList();

        List<String> indicators = indicatorRepository
                .findByStudentProfileId(studentProfileId)
                .stream()
                .map(indicator -> indicator.getIndicatorCode()
                        + " | value="
                        + indicator.getIndicatorValue()
                        + " | "
                        + indicator.getInterpretation())
                .toList();

        List<String> diagnoses = diagnosisRepository
                .findByStudentProfileId(studentProfileId)
                .stream()
                .map(diagnosis -> diagnosis.getDiagnosisCategory()
                        + " | confidence="
                        + diagnosis.getConfidenceScore()
                        + " | "
                        + diagnosis.getDiagnosisSummary())
                .toList();

        List<String> recommendations = recommendationRepository
                .findByStudentProfileId(studentProfileId)
                .stream()
                .map(recommendation -> recommendation.getDuaPrinciple()
                        + " | priority="
                        + recommendation.getPriorityScore()
                        + " | "
                        + recommendation.getRecommendationText())
                .toList();

        List<String> summary = List.of(
                "Responses provide educational evidence.",
                "Indicators transform responses into interpretable learning signals.",
                "Diagnoses consolidate indicators into inclusive learning interpretation.",
                "DUA recommendations translate diagnosis into pedagogical intervention."
        );

        return new EvidenceTraceabilityResponse(
                studentProfileId.toString(),
                responses,
                indicators,
                diagnoses,
                recommendations,
                summary
        );
    }
}
