package com.inclusive.diagnosis.engine.service;

import com.inclusive.diagnosis.diagnosis.entity.InclusiveDiagnosis;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import com.inclusive.diagnosis.dua.entity.DuaRecommendation;
import com.inclusive.diagnosis.dua.repository.DuaRecommendationRepository;
import com.inclusive.diagnosis.indicator.entity.LearningIndicator;
import com.inclusive.diagnosis.indicator.repository.LearningIndicatorRepository;
import com.inclusive.diagnosis.response.entity.DiagnosticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EducationalRuleEngineService {

    private final LearningIndicatorRepository learningIndicatorRepository;

    private final InclusiveDiagnosisRepository inclusiveDiagnosisRepository;

    private final DuaRecommendationRepository duaRecommendationRepository;

    public void processResponse(
            DiagnosticResponse response
    ) {

        if ("REFLECTIVE_OBSERVATION".equals(
                response.getResponseValue()
        )) {

            LearningIndicator indicator =
                    LearningIndicator.builder()
                            .tenantId(response.getTenantId())
                            .studentProfileId(
                                    response.getStudentProfileId()
                            )
                            .indicatorCode(
                                    "REFLECTIVE_OBSERVATION"
                            )
                            .indicatorCategory(
                                    "LEARNING_STYLE"
                            )
                            .indicatorValue(0.82)
                            .interpretation(
                                    "Strong reflective preference"
                            )
                            .pedagogicalRecommendation(
                                    "Use reflective and analytical activities"
                            )
                            .calculatedAt(LocalDateTime.now())
                            .build();

            LearningIndicator savedIndicator =
                    learningIndicatorRepository.save(indicator);

            InclusiveDiagnosis diagnosis =
                    generateDiagnosis(savedIndicator);

            if (diagnosis != null) {
                generateDuaRecommendation(diagnosis);
            }
        }
    }

    private InclusiveDiagnosis generateDiagnosis(
            LearningIndicator indicator
    ) {

        if ("REFLECTIVE_OBSERVATION".equals(
                indicator.getIndicatorCode()
        ) && indicator.getIndicatorValue() >= 0.8) {

            InclusiveDiagnosis diagnosis =
                    InclusiveDiagnosis.builder()
                            .tenantId(indicator.getTenantId())
                            .studentProfileId(
                                    indicator.getStudentProfileId()
                            )
                            .diagnosisCategory(
                                    "INCLUSIVE_LEARNING_PROFILE"
                            )
                            .diagnosisSummary(
                                    "Student demonstrates a strong reflective learning profile."
                            )
                            .identifiedBarriers(
                                    "Potential low classroom interaction"
                            )
                            .learningStrengths(
                                    "Reflective analysis and independent learning"
                            )
                            .supportNeeds(
                                    "Structured reflective activities"
                            )
                            .recommendedInterventions(
                                    "Use guided reflective journals and asynchronous learning"
                            )
                            .dueAlignment(
                                    "Multiple means of engagement"
                            )
                            .confidenceScore(0.91)
                            .generatedAt(LocalDateTime.now())
                            .build();

            return inclusiveDiagnosisRepository.save(diagnosis);
        }

        return null;
    }

    private void generateDuaRecommendation(
            InclusiveDiagnosis diagnosis
    ) {

        DuaRecommendation recommendation =
                DuaRecommendation.builder()
                        .tenantId(diagnosis.getTenantId())
                        .studentProfileId(
                                diagnosis.getStudentProfileId()
                        )
                        .duaPrinciple(
                                "MULTIPLE_MEANS_OF_ENGAGEMENT"
                        )
                        .recommendationCategory(
                                "REFLECTIVE_LEARNING"
                        )
                        .recommendationText(
                                "Use reflective journals, asynchronous discussions and guided metacognitive prompts."
                        )
                        .accessibilitySupport(
                                "Flexible pacing and clear written instructions"
                        )
                        .assistiveTechnologySuggestion(
                                "Speech-to-text or digital journaling tools"
                        )
                        .implementationGuidance(
                                "Provide weekly guided reflection prompts and allow asynchronous participation."
                        )
                        .priorityScore(0.95)
                        .generatedAt(LocalDateTime.now())
                        .build();

        duaRecommendationRepository.save(recommendation);
    }
}
