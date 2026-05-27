package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DoctoralContributionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctoralContributionService {

    public DoctoralContributionResponse generateContributionPreview() {

        return new DoctoralContributionResponse(
                "HIGH_DOCTORAL_CONTRIBUTION",
                "Ethical, governed, explainable and longitudinal adaptive educational intelligence for inclusive education.",
                List.of(
                        "Non-clinical educational AI",
                        "Explainable inclusive support intelligence",
                        "Longitudinal adaptive educational modeling",
                        "Human-centered teacher-assisted AI",
                        "Governed and auditable educational AI",
                        "Pilot-ready doctoral validation framework"
                ),
                "STRONG_ORIGINALITY",
                "HIGHLY_DEFENSIBLE",
                "Prepare thesis contribution section linking architecture, ethics, validation and educational impact."
        );
    }
}
