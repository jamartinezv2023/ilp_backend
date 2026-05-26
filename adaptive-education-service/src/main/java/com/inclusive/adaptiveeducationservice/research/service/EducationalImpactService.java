package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalImpactResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalImpactService {

    public EducationalImpactResponse generateEducationalImpact() {

        return new EducationalImpactResponse(
                "HIGH_POTENTIAL_IMPACT",
                "ENHANCED_TEACHER_DECISION_SUPPORT",
                "STRENGTHENED_INCLUSIVE_PARTICIPATION",
                "EVIDENCE_BASED_GOVERNANCE_ENABLED",
                List.of(
                        "Reduced teacher uncertainty in inclusive support planning",
                        "Improved PIAR-oriented pedagogical decision support",
                        "Longitudinal monitoring of adaptive interventions",
                        "Transparent and explainable educational AI recommendations",
                        "Ethical non-clinical support intelligence"
                ),
                "SIGNIFICANT_DOCTORAL_CONTRIBUTION"
        );
    }
}
