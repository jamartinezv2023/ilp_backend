package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ArchitectureRemediationRoadmapResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchitectureRemediationRoadmapService {

    public ArchitectureRemediationRoadmapResponse generateRemediationRoadmapPreview() {

        return new ArchitectureRemediationRoadmapResponse(
                "READY_FOR_ENGINEERING_HARDENING",
                "HIGH_PRIORITY",
                List.of(
                        "Formalize OpenAPI contract documentation",
                        "Create Dockerfile and docker-compose deployment profile",
                        "Generate C4 architecture diagrams",
                        "Create ADR documents for major architecture decisions",
                        "Finalize SonarCloud Quality Gate evidence"
                ),
                List.of(
                        "Introduce HATEOAS links for key REST resources",
                        "Prepare Kubernetes deployment manifests",
                        "Document API versioning policy",
                        "Add dependency vulnerability scanning",
                        "Create architecture evidence matrix for evaluators"
                ),
                List.of(
                        "OpenAPI JSON/YAML contract",
                        "Docker build evidence",
                        "C4 diagrams",
                        "ADR markdown files",
                        "SonarCloud dashboard evidence",
                        "CI/CD execution logs"
                ),
                "Prioritize OpenAPI, Docker, C4 diagrams, ADR documentation and SonarCloud quality evidence in the next engineering sprint."
        );
    }
}
