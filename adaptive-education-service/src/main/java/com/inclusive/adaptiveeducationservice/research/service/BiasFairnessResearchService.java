package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.BiasFairnessAssessmentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BiasFairnessResearchService {

    public BiasFairnessAssessmentResponse generateBiasFairnessAssessment() {

        return new BiasFairnessAssessmentResponse(
                "MONITORED",
                "LOW_CONTROLLED",
                List.of(
                        "Non-discrimination",
                        "Inclusive access",
                        "Reasonable educational adjustment",
                        "Human-centered pedagogical decision"
                ),
                "Review adaptive recommendations for unfair educational disadvantage before institutional use.",
                "MANDATORY",
                "Fairness assessment excludes clinical labels and protects inclusive educational decision-making."
        );
    }
}
