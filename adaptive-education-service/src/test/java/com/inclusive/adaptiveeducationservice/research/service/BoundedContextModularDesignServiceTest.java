package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoundedContextModularDesignServiceTest {

    private final BoundedContextModularDesignService service =
            new BoundedContextModularDesignService();

    @Test
    void shouldGenerateBoundedContextModularDesignPreview() {
        var response = service.generateBoundedContextPreview();

        assertThat(response.modularDesignLevel()).isEqualTo("HIGH_MODULAR_COHERENCE");
        assertThat(response.boundedContextStatus()).contains("BOUNDED_CONTEXTS");
        assertThat(response.identifiedBoundedContexts()).isNotEmpty();
        assertThat(response.responsibilitySeparationStatus()).contains("RESPONSIBILITIES_SEPARATED");
        assertThat(response.domainAlignmentStatus()).contains("INCLUSIVE_EDUCATIONAL_INTELLIGENCE");
        assertThat(response.recommendedModularAction()).contains("context map");
    }
}
