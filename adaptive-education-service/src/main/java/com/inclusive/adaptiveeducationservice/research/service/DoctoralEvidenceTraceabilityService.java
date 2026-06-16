package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DoctoralEvidenceTraceabilityResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctoralEvidenceTraceabilityService {

    public DoctoralEvidenceTraceabilityResponse generateTraceabilityPreview() {

        return new DoctoralEvidenceTraceabilityResponse(
                "FULL_DOCTORAL_TRACEABILITY",
                "VALIDATED_BY_BUILD_AND_ENDPOINTS",
                "SUPPORTED_BY_EXPERIMENTAL_DESIGN",
                "SUPPORTED_BY_NON_CLINICAL_AI_POLICY",
                List.of(
                        "Build successful evidence",
                        "Endpoint validation evidence",
                        "Git commit history",
                        "Ethical AI policy outputs",
                        "Governance and XAI previews",
                        "Pilot validation readiness",
                        "Evaluation metrics readiness"
                ),
                "Document validated endpoints, commits, ethical safeguards and methodological alignment in the doctoral progress report."
        );
    }
}
