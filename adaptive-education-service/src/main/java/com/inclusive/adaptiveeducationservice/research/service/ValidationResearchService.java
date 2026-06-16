package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ValidationPreviewResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationResearchService {

    public ValidationPreviewResponse generateValidationPreview() {

        return new ValidationPreviewResponse(
                "PRE_EXPERIMENTAL_PHASE",
                List.of(
                        "Psychopedagogical profiling",
                        "PIAR support intelligence",
                        "Temporal educational intelligence",
                        "Adaptive recommendation system"
                ),
                List.of(
                        "Explainability",
                        "Pedagogical coherence",
                        "Ethical compliance",
                        "Intervention traceability"
                ),
                "HIGH",
                "Start pilot validation with anonymized educational datasets"
        );
    }
}
