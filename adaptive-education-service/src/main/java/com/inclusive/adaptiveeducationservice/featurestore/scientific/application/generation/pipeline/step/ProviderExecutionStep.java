package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.step;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.ScientificGenerationContext;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline.ScientificGenerationPipelineStep;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.provider.ScientificFeatureProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ProviderExecutionStep
        implements ScientificGenerationPipelineStep {

    private final List<ScientificFeatureProvider> providers;

    public ProviderExecutionStep(
            List<ScientificFeatureProvider> providers
    ) {
        Objects.requireNonNull(
                providers,
                "providers are required"
        );

        if (providers.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "providers must not contain null elements"
            );
        }

        this.providers = List.copyOf(providers);
    }

    @Override
    public ScientificGenerationContext execute(
            ScientificGenerationContext context
    ) {
        Objects.requireNonNull(
                context,
                "context is required"
        );

        List<ScientificFeatureItem> accumulated =
                new ArrayList<>(context.features());

        for (ScientificFeatureProvider provider : providers) {
            List<ScientificFeatureItem> provided =
                    Objects.requireNonNull(
                            provider.provide(context.request()),
                            "provider result must not be null"
                    );

            if (provided.stream().anyMatch(Objects::isNull)) {
                throw new IllegalArgumentException(
                        "provider result must not contain null elements"
                );
            }

            accumulated.addAll(provided);
        }

        return context.withFeatures(accumulated);
    }
}