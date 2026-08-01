package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.orchestration;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.idempotency.ScientificGenerationIdempotencyService;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScientificGenerationOrchestrationConfiguration {

    @Bean
    ScientificGenerationIdempotencyService
    scientificGenerationIdempotencyService(
            ScientificFeatureVectorQueryPort queryPort
    ) {
        return new ScientificGenerationIdempotencyService(
                queryPort
        );
    }
}