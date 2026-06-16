package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.ArchitectureDecisionRecordsResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchitectureDecisionRecordsService {

    public ArchitectureDecisionRecordsResponse generateAdrPreview() {

        return new ArchitectureDecisionRecordsResponse(
                "ADR_DOCUMENTATION_REQUIRED",
                "TRACEABLE_BY_GIT_AND_MVP_HISTORY",
                List.of(
                        "Hexagonal architecture adoption",
                        "Modular Gradle multi-project structure",
                        "PostgreSQL runtime validation",
                        "OpenAPI contract readiness",
                        "DevSecOps quality automation",
                        "Ethical AI governance boundaries"
                ),
                "RATIONALE_IDENTIFIED_BUT_FORMAL_ADR_FILES_PENDING",
                "CONTROLLED_WITH_ARCHITECTURE_TESTS_AND_QUALITY_PREVIEWS",
                "Create /docs/adr records for major architecture, quality, security, AI governance and deployment decisions."
        );
    }
}
