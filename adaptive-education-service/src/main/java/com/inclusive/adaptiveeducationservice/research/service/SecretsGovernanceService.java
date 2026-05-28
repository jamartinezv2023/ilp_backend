package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.SecretsGovernanceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecretsGovernanceService {

    public SecretsGovernanceResponse generateSecretsGovernancePreview() {

        return new SecretsGovernanceResponse(
                "SECRETS_GOVERNANCE_REQUIRED",
                "CONTROLLED_AFTER_EXTERNALIZATION",
                "ENVIRONMENT_VARIABLES_AND_CI_CD_SECRETS_REQUIRED",
                "READY_FOR_AZURE_DEVOPS_OR_GITHUB_ACTIONS_SECRETS",
                "GIT_IGNORE_AND_SECRET_SCANNING_REQUIRED",
                List.of(
                        "Datasource credentials currently injectable through runtime arguments",
                        "PostgreSQL runtime validated",
                        "CI/CD pipeline readiness available",
                        "Secure runtime configuration preview implemented",
                        "DevSecOps quality intelligence implemented",
                        "OpenAPI governance available"
                ),
                "Move datasource credentials to environment variables, configure CI/CD secret storage, enable secret scanning and ensure sensitive files are excluded from Git."
        );
    }
}
