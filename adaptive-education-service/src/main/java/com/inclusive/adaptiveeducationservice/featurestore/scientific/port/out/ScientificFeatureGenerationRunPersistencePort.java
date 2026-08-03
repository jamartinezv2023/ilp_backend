package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationRun;

public interface ScientificFeatureGenerationRunPersistencePort {

    ScientificFeatureGenerationRun save(
            ScientificFeatureGenerationRun run
    );
}