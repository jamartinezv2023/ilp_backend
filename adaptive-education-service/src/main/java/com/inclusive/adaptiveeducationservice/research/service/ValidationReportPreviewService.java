package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ValidationReportPreviewResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationReportPreviewService {

    public ValidationReportPreviewResponse generateValidationReportPreview() {

        return new ValidationReportPreviewResponse(
                "READY_FOR_DOCTORAL_REVIEW",
                "HIGH",
                "COHERENT",
                "VALIDATED",
                List.of(
                        "Validation intelligence",
                        "Experimental design intelligence",
                        "Explainability intelligence",
                        "Governance intelligence",
                        "Trustworthy AI intelligence",
                        "Fairness intelligence",
                        "Observability intelligence",
                        "Human-centered AI intelligence",
                        "Pilot validation readiness",
                        "Evaluation metrics intelligence"
                ),
                "Prepare supervised pilot protocol and doctoral validation chapter."
        );
    }
}
