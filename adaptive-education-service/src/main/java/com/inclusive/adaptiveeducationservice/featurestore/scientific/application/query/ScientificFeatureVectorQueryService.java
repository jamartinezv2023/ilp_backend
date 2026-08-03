package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.application.query.mapper.ScientificFeatureVectorResultMapper;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.ScientificFeatureVectorQueryUseCase;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureVectorResult;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;

import java.util.Objects;
import java.util.Optional;

public final class ScientificFeatureVectorQueryService
        implements ScientificFeatureVectorQueryUseCase {

    private final ScientificFeatureVectorQueryPort queryPort;

    private final ScientificFeatureVectorResultMapper
            resultMapper;

    public ScientificFeatureVectorQueryService(
            ScientificFeatureVectorQueryPort queryPort,
            ScientificFeatureVectorResultMapper resultMapper
    ) {
        this.queryPort =
                Objects.requireNonNull(
                        queryPort,
                        "queryPort is required"
                );

        this.resultMapper =
                Objects.requireNonNull(
                        resultMapper,
                        "resultMapper is required"
                );
    }

    @Override
    public Optional<ScientificFeatureVectorResult> findExact(
            FindExactScientificFeatureVectorQuery query
    ) {
        Objects.requireNonNull(
                query,
                "query is required"
        );

        Optional<ScientificFeatureVector> vector =
                Objects.requireNonNull(
                        queryPort.findExact(
                                query.participantId(),
                                query.featureSetVersion(),
                                query.featureCutoffAt()
                        ),
                        "queryPort result must not be null"
                );

        return vector.map(
                resultMapper::toResult
        );
    }

    @Override
    public Optional<ScientificFeatureVectorResult>
    findLatestCompleted(
            FindLatestScientificFeatureVectorQuery query
    ) {
        Objects.requireNonNull(
                query,
                "query is required"
        );

        Optional<ScientificFeatureVector> vector =
                Objects.requireNonNull(
                        queryPort.findLatestCompleted(
                                query.participantId(),
                                query.featureSetVersion()
                        ),
                        "queryPort result must not be null"
                );

        return vector.map(
                resultMapper::toResult
        );
    }
}
