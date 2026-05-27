package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.DoctoralThesisReadinessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctoralThesisReadinessService {

    public DoctoralThesisReadinessResponse generateThesisReadinessPreview() {

        return new DoctoralThesisReadinessResponse(
                "READY_FOR_DOCTORAL_PROGRESS_REVIEW",
                "HIGH",
                "VALIDATED_BY_BUILD_AND_ENDPOINTS",
                "NON_CLINICAL_ETHICAL_AI_BOUNDARY_CONFIRMED",
                List.of(
                        "Problem statement",
                        "Research objectives",
                        "Methodological framework",
                        "System architecture",
                        "Ethical AI governance",
                        "Validation strategy",
                        "Expected scientific contribution"
                ),
                "Prepare doctoral progress report and presentation for thesis advisor review."
        );
    }
}
