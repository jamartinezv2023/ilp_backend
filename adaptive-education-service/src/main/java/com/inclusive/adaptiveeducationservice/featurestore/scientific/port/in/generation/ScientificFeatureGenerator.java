package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;

@FunctionalInterface
public interface ScientificFeatureGenerator {

    ScientificFeatureVector generate(
            ScientificFeatureGenerationRequest request
    );
}