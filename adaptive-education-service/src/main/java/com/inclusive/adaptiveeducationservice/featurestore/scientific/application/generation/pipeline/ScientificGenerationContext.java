package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.pipeline;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;

import java.util.List;
import java.util.Objects;

public record ScientificGenerationContext(
        ScientificFeatureGenerationRequest request,
        List<ScientificFeatureItem> features,
        ScientificFeatureVector result
) {

    public ScientificGenerationContext {
        Objects.requireNonNull(request, "request is required");
        Objects.requireNonNull(features, "features are required");

        if (features.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "features must not contain null elements"
            );
        }

        features = List.copyOf(features);
    }

    public static ScientificGenerationContext initial(
            ScientificFeatureGenerationRequest request
    ) {
        return new ScientificGenerationContext(
                request,
                List.of(),
                null
        );
    }

    public ScientificGenerationContext withFeatures(
            List<ScientificFeatureItem> newFeatures
    ) {
        return new ScientificGenerationContext(
                request,
                newFeatures,
                result
        );
    }

    public ScientificGenerationContext withResult(
            ScientificFeatureVector newResult
    ) {
        return new ScientificGenerationContext(
                request,
                features,
                Objects.requireNonNull(
                        newResult,
                        "result is required"
                )
        );
    }

    public boolean hasResult() {
        return result != null;
    }
}