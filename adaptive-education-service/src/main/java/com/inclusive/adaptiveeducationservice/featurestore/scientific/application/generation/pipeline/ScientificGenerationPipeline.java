package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;

import java.util.List;
import java.util.Objects;

public final class ScientificGenerationPipeline {

    private final List<ScientificGenerationPipelineStep> steps;

    public ScientificGenerationPipeline(
            List<ScientificGenerationPipelineStep> steps
    ) {
        Objects.requireNonNull(
                steps,
                "steps are required"
        );

        if (steps.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one pipeline step is required"
            );
        }

        if (steps.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "steps must not contain null elements"
            );
        }

        this.steps = List.copyOf(steps);
    }

    public ScientificFeatureVector execute(
            ScientificFeatureGenerationRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request is required"
        );

        ScientificGenerationContext context =
                ScientificGenerationContext.initial(
                        request
                );

        for (
                ScientificGenerationPipelineStep step
                : steps
        ) {
            context =
                    Objects.requireNonNull(
                            step.execute(context),
                            "pipeline step result must not be null"
                    );
        }

        if (!context.hasResult()) {
            throw new IllegalStateException(
                    "Pipeline completed without a scientific feature vector"
            );
        }

        return context.result();
    }
}