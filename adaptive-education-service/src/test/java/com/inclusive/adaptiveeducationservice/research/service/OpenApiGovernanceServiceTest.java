package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiGovernanceServiceTest {

    private final OpenApiGovernanceService service =
            new OpenApiGovernanceService();

    @Test
    void shouldGenerateOpenApiGovernancePreview() {
        var response = service.generateOpenApiGovernancePreview();

        assertThat(response.openApiStatus()).isEqualTo("OPENAPI_3_AVAILABLE");
        assertThat(response.swaggerUiStatus()).isEqualTo("SWAGGER_UI_OPERATIONAL");
        assertThat(response.apiDiscoverability()).contains("DISCOVERABLE");
        assertThat(response.contractGovernanceLevel()).contains("CONTRACT_GOVERNANCE");
        assertThat(response.contractEvidence()).isNotEmpty();
        assertThat(response.recommendedOpenApiAction()).contains("OpenAPI JSON");
    }
}
