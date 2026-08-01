package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.step;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.ScientificGenerationContext;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.ScientificGenerationPipelineStep;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;

import java.util.Objects;

public final class VectorAssemblyStep
        implements ScientificGenerationPipelineStep {

    @Override
    public ScientificGenerationContext execute(
            ScientificGenerationContext context
    ) {
        Objects.requireNonNull(
                context,
                "context is required"
        );

        ScientificFeatureGenerationRequest request =
                context.request();

        ScientificFeatureVector vector =
                new ScientificFeatureVector(
                        request.vectorId(),
                        request.participantId(),
                        request.featureSetVersion(),
                        request.generatorVersion(),
                        request.featureCutoffAt(),
                        request.generatedAt(),
                        request.inputObservationCount(),
                        request.checksum(),
                        context.features()
                );

        return context.withResult(vector);
    }
}