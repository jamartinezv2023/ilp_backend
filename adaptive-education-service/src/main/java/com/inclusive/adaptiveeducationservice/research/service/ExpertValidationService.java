package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ExpertValidationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpertValidationService {

    public ExpertValidationResponse generateExpertValidationPreview() {

        return new ExpertValidationResponse(
                "READY_FOR_EXPERT_REVIEW_PREPARATION",
                "EDUCATIONAL_RESEARCH_AND_PEDAGOGICAL_INNOVATION",
                "INCLUSIVE_EDUCATION_AND_REASONABLE_ADJUSTMENTS",
                "RESPONSIBLE_AI_ETHICS_AND_HUMAN_OVERSIGHT",
                "EDUCATIONAL_TECHNOLOGY_AND_BACKEND_ARCHITECTURE",
                List.of(
                        "Pedagogical coherence",
                        "Inclusive education relevance",
                        "Ethical non-clinical AI boundary",
                        "Explainability and interpretability",
                        "Teacher usability",
                        "Research validity",
                        "Technological feasibility"
                ),
                "Prepare expert validation rubric, select interdisciplinary reviewers and collect qualitative feedback before pilot implementation."
        );
    }
}
