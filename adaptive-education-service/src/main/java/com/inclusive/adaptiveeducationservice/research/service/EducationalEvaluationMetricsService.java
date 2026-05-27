package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalEvaluationMetricsResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalEvaluationMetricsService {

    public EducationalEvaluationMetricsResponse generateEvaluationMetricsPreview() {

        return new EducationalEvaluationMetricsResponse(
                "READY_FOR_PILOT_EVALUATION",
                List.of(
                        "Recommendation usefulness score",
                        "Teacher adoption rate",
                        "Intervention follow-up completion",
                        "Support evolution improvement",
                        "Explainability acceptance score"
                ),
                List.of(
                        "Teacher perceived usefulness",
                        "Pedagogical coherence perception",
                        "Institutional trust perception",
                        "Ethical clarity perception"
                ),
                List.of(
                        "Non-clinical boundary respected",
                        "Human oversight preserved",
                        "Bias and fairness reviewed",
                        "Student data protection ensured"
                ),
                "MANDATORY",
                "SUPERVISED_MIXED_METHOD_PILOT_EVALUATION"
        );
    }
}
