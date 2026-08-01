package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline;

@FunctionalInterface
public interface ScientificGenerationPipelineStep {

    ScientificGenerationContext execute(
            ScientificGenerationContext context
    );
}