package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiSecurityGovernanceServiceTest {

    private final ApiSecurityGovernanceService service =
            new ApiSecurityGovernanceService();

    @Test
    void shouldGenerateApiSecurityGovernancePreview() {
        var response = service.generateSecurityGovernancePreview();

        assertThat(response.apiSecurityGovernanceStatus()).contains("SECURITY_GOVERNANCE");
        assertThat(response.authenticationReadiness()).contains("JWT");
        assertThat(response.authorizationReadiness()).contains("ROLE_BASED_ACCESS_CONTROL");
        assertThat(response.contractSecurityStatus()).contains("OPENAPI");
        assertThat(response.securityEvidence()).isNotEmpty();
        assertThat(response.recommendedSecurityAction()).contains("OAuth2");
    }
}
