package com.inclusive.diagnosis.engine.service;

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

            learningIndicatorRepository.save(
                    indicator
            );
        }
    }
}
