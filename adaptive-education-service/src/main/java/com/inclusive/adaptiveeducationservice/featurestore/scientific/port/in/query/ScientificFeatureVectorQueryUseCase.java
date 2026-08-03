package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureVectorResult;

import java.util.Optional;

public interface ScientificFeatureVectorQueryUseCase {

    Optional<ScientificFeatureVectorResult> findExact(
            FindExactScientificFeatureVectorQuery query
    );

    Optional<ScientificFeatureVectorResult> findLatestCompleted(
            FindLatestScientificFeatureVectorQuery query
    );
}
