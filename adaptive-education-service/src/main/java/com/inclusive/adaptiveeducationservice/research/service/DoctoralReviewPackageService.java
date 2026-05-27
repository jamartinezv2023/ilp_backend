package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DoctoralReviewPackageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctoralReviewPackageService {

    public DoctoralReviewPackageResponse generateReviewPackagePreview() {

        return new DoctoralReviewPackageResponse(
                "READY_FOR_ADVISOR_REVIEW",
                "HIGH",
                List.of(
                        "Functional endpoint evidence",
                        "Build successful evidence",
                        "Git commit traceability",
                        "Validation intelligence outputs",
                        "Experimental design outputs",
                        "XAI, governance, trustworthiness and fairness outputs",
                        "Pilot validation and evaluation readiness"
                ),
                List.of(
                        "Prepare formal doctoral progress report",
                        "Update thesis presentation",
                        "Define supervised pilot validation protocol",
                        "Prepare ethics and data protection annex"
                ),
                "Discuss scientific contribution, validation strategy and ethical non-clinical AI boundaries.",
                "Advisor-approved supervised pilot validation phase"
        );
    }
}
