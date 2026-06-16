package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EnterpriseArchitectureGovernanceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnterpriseArchitectureGovernanceService {

    public EnterpriseArchitectureGovernanceResponse generateGovernancePreview() {

        return new EnterpriseArchitectureGovernanceResponse(
                "ACTIVE_ARCHITECTURE_GOVERNANCE",
                "HEXAGONAL_MODULAR_ARCHITECTURE",
                "BOUNDED_CONTEXTS_IDENTIFIED_BY_EDUCATIONAL_INTELLIGENCE_CAPABILITIES",
                "API_APPLICATION_DOMAIN_INFRASTRUCTURE_SEPARATION_ENFORCED",
                "ARCHITECTURE_TESTS_AND_LAYER_RULES_ACTIVE",
                "ENTERPRISE_SCALE_READY_AFTER_CONTRACT_AND_DEPLOYMENT_HARDENING",
                List.of(
                        "Hexagonal architecture test enforcement",
                        "Modular Gradle multi-project structure",
                        "API layer separated from research services",
                        "DTO-based response contracts",
                        "Git traceability by MVP",
                        "Quality intelligence endpoints available",
                        "CI/CD and DevSecOps readiness evidence"
                ),
                "Formalize bounded context documentation, architecture decision records, OpenAPI contracts and deployment topology diagrams."
        );
    }
}
