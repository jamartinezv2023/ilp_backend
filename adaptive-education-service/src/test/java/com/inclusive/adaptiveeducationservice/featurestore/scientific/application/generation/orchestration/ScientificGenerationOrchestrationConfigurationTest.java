package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.orchestration;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.idempotency.ScientificGenerationIdempotencyService;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ScientificGenerationOrchestrationConfigurationTest {

    @Test
    void shouldRegisterIdempotencyServiceBean() {
        try (
                AnnotationConfigApplicationContext context =
                        new AnnotationConfigApplicationContext()
        ) {
            context.registerBean(
                    ScientificFeatureVectorQueryPort.class,
                    () -> mock(
                            ScientificFeatureVectorQueryPort.class
                    )
            );

            context.register(
                    ScientificGenerationOrchestrationConfiguration.class
            );

            context.refresh();

            assertThat(
                    context.getBean(
                            ScientificGenerationIdempotencyService.class
                    )
            ).isNotNull();
        }
    }
}