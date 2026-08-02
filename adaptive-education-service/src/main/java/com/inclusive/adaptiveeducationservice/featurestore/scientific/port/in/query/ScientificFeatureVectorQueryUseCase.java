package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;

import java.util.Optional;

public interface ScientificFeatureVectorQueryUseCase {

    Optional<ScientificFeatureVector> findExact(
            FindExactScientificFeatureVectorQuery query
    );

    Optional<ScientificFeatureVector> findLatestCompleted(
            FindLatestScientificFeatureVectorQuery query
    );
}