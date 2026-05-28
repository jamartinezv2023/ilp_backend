package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ArchitectureEvaluationMatrixResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchitectureEvaluationMatrixService {

    public ArchitectureEvaluationMatrixResponse generateEvaluationMatrixPreview() {

        return new ArchitectureEvaluationMatrixResponse(
                "READY_FOR_EVALUATOR_MAPPING",
                "HIGH_ALIGNMENT_WITH_BACKEND_AND_ARCHITECTURE_RUBRIC",
                List.of(
                        "Architecture solution design",
                        "Backend functional compliance",
                        "API contract readiness",
                        "REST endpoint evidence",
                        "Deployment readiness",
                        "Quality gate readiness",
                        "CI/CD readiness",
                        "Architecture documentation readiness"
                ),
                List.of(
                        "Hexagonal modular architecture",
                        "Gradle multi-module structure",
                        "PostgreSQL runtime validation",
                        "Validated REST endpoints",
                        "Jacoco coverage verification",
                        "SpotBugs static analysis",
                        "Git commit traceability",
                        "CI/CD pipeline readiness preview"
                ),
                List.of(
                        "Formal OpenAPI documentation",
                        "HATEOAS links",
                        "Dockerfile and docker-compose",
                        "Kubernetes manifests",
                        "C4 diagrams",
                        "ADR documents",
                        "Final SonarCloud Quality Gate evidence"
                ),
                "Use this matrix to prepare the architecture evaluation section and attach endpoint, build, quality and deployment evidence."
        );
    }
}
