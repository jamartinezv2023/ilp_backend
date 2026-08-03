package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.step;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.ScientificGenerationContext;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.ScientificGenerationPipelineStep;

import java.util.Objects;

public final class SeedFeaturesStep
        implements ScientificGenerationPipelineStep {

    @Override
    public ScientificGenerationContext execute(
            ScientificGenerationContext context
    ) {
        Objects.requireNonNull(
                context,
                "context is required"
        );

        return context.withFeatures(
                context.request().extractedFeatures()
        );
    }
}