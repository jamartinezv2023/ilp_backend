package com.inclusive.diagnosis.engine.service;

import com.inclusive.diagnosis.diagnosis.entity.InclusiveDiagnosis;
import com.inclusive.diagnosis.diagnosis.repository.InclusiveDiagnosisRepository;
import com.inclusive.diagnosis.indicator.entity.LearningIndicator;
import com.inclusive.diagnosis.indicator.repository.LearningIndicatorRepository;
import com.inclusive.diagnosis.response.entity.DiagnosticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EducationalRuleEngineService {

    private final LearningIndicatorRepository
            learningIndicatorRepository;

    private final InclusiveDiagnosisRepository
            inclusiveDiagnosisRepository;

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
                            .calculatedAt(
                                    LocalDateTime.now()
                            )
                            .build();

            LearningIndicator savedIndicator =
                    learningIndicatorRepository.save(
                            indicator
                    );

            generateDiagnosis(
                    savedIndicator
            );
        }
    }

    private void generateDiagnosis(
            LearningIndicator indicator
    ) {

        if ("REFLECTIVE_OBSERVATION".equals(
                indicator.getIndicatorCode()
        ) && indicator.getIndicatorValue() >= 0.8) {

            InclusiveDiagnosis diagnosis =
                    InclusiveDiagnosis.builder()
                            .tenantId(
                                    indicator.getTenantId()
                            )
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
                            .generatedAt(
                                    LocalDateTime.now()
                            )
                            .build();

            inclusiveDiagnosisRepository.save(
                    diagnosis
            );
        }
    }
}
