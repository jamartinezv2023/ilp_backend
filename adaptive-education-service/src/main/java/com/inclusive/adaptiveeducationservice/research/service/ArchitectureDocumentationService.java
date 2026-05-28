package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ArchitectureDocumentationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchitectureDocumentationService {

    public ArchitectureDocumentationResponse generateDocumentationPreview() {

        return new ArchitectureDocumentationResponse(
                "DOCUMENTATION_REQUIRED_AND_TRACEABLE",
                "C4_AND_DEPLOYMENT_DIAGRAMS_PENDING",
                "C4_MODEL_RECOMMENDED",
                List.of(
                        "System context view",
                        "Container view",
                        "Component view",
                        "Deployment view",
                        "Quality and governance view",
                        "AI ethics and observability view"
                ),
                "READY_FOR_ARCHITECTURE_REVIEW_AFTER_DIAGRAMS",
                "Create C4 diagrams, deployment diagrams, context map and quality evidence matrix for evaluator review."
        );
    }
}
