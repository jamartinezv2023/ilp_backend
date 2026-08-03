package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureGenerationRun;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ScientificFeatureGenerationRunId;

import java.util.Optional;

public interface ScientificFeatureGenerationRunQueryPort {

    Optional<ScientificFeatureGenerationRun> findById(
            ScientificFeatureGenerationRunId runId
    );
}