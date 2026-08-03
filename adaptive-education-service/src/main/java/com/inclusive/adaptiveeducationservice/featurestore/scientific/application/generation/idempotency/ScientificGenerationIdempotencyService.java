package com.inclusive.adaptiveeducationservice.featurestore.scientific.application.generation.idempotency;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.ScientificFeatureVectorQueryPort;

import java.util.Objects;
import java.util.Optional;

public final class ScientificGenerationIdempotencyService {

    private final ScientificFeatureVectorQueryPort queryPort;

    public ScientificGenerationIdempotencyService(
            ScientificFeatureVectorQueryPort queryPort
    ) {
        this.queryPort =
                Objects.requireNonNull(
                        queryPort,
                        "queryPort is required"
                );
    }

    public ScientificGenerationDecision decide(
            ScientificFeatureGenerationRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request is required"
        );

        Optional<ScientificFeatureVector> existingVector =
                queryPort.findExact(
                        request.participantId(),
                        request.featureSetVersion(),
                        request.featureCutoffAt()
                );

        return existingVector
                .map(ScientificGenerationDecision::reuse)
                .orElseGet(
                        ScientificGenerationDecision::generateNew
                );
    }
}