package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;

public interface ScientificFeatureVectorPersistencePort {

    ScientificFeatureVector save(
            ScientificFeatureVector vector
    );
}