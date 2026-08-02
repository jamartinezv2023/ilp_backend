package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.ScientificFeatureVectorQueryUseCase;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;

import java.util.Objects;
import java.util.Optional;

public final class ScientificFeatureVectorQueryService
        implements ScientificFeatureVectorQueryUseCase {

    private final ScientificFeatureVectorQueryPort queryPort;

    public ScientificFeatureVectorQueryService(
            ScientificFeatureVectorQueryPort queryPort
    ) {
        this.queryPort =
                Objects.requireNonNull(
                        queryPort,
                        "queryPort is required"
                );
    }

    @Override
    public Optional<ScientificFeatureVector> findExact(
            FindExactScientificFeatureVectorQuery query
    ) {
        Objects.requireNonNull(
                query,
                "query is required"
        );

        return Objects.requireNonNull(
                queryPort.findExact(
                        query.participantId(),
                        query.featureSetVersion(),
                        query.featureCutoffAt()
                ),
                "queryPort result must not be null"
        );
    }

    @Override
    public Optional<ScientificFeatureVector> findLatestCompleted(
            FindLatestScientificFeatureVectorQuery query
    ) {
        Objects.requireNonNull(
                query,
                "query is required"
        );

        return Objects.requireNonNull(
                queryPort.findLatestCompleted(
                        query.participantId(),
                        query.featureSetVersion()
                ),
                "queryPort result must not be null"
        );
    }
}