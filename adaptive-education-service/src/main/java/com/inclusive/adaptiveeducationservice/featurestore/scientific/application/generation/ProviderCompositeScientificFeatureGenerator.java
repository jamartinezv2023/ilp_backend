package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.ScientificFeatureGenerator;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.provider.ScientificFeatureProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public final class ProviderCompositeScientificFeatureGenerator
        implements ScientificFeatureGenerator {

    private final List<ScientificFeatureProvider> providers;

    public ProviderCompositeScientificFeatureGenerator(
            List<ScientificFeatureProvider> providers
    ) {
        Objects.requireNonNull(
                providers,
                "providers must not be null"
        );

        if (providers.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "providers must not contain null elements"
            );
        }

        this.providers = List.copyOf(providers);
    }

    @Override
    public ScientificFeatureVector generate(
            ScientificFeatureGenerationRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request must not be null"
        );

        List<ScientificFeatureItem> generatedFeatures =
                new ArrayList<>(
                        request.extractedFeatures()
                );

        for (ScientificFeatureProvider provider : providers) {
            List<ScientificFeatureItem> providedFeatures =
                    Objects.requireNonNull(
                            provider.provide(request),
                            "provider result must not be null"
                    );

            if (
                    providedFeatures.stream()
                            .anyMatch(Objects::isNull)
            ) {
                throw new IllegalArgumentException(
                        "provider result must not contain null elements"
                );
            }

            generatedFeatures.addAll(providedFeatures);
        }

        return new ScientificFeatureVector(
                request.vectorId(),
                request.participantId(),
                request.featureSetVersion(),
                request.generatorVersion(),
                request.featureCutoffAt(),
                request.generatedAt(),
                request.inputObservationCount(),
                request.checksum(),
                generatedFeatures
        );
    }
}