package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;

import java.time.Instant;
import java.util.Objects;

public record FindExactScientificFeatureVectorQuery(
        ParticipantId participantId,
        FeatureSetVersion featureSetVersion,
        Instant featureCutoffAt
) {

    public FindExactScientificFeatureVectorQuery {
        Objects.requireNonNull(
                participantId,
                "participantId is required"
        );

        Objects.requireNonNull(
                featureSetVersion,
                "featureSetVersion is required"
        );

        Objects.requireNonNull(
                featureCutoffAt,
                "featureCutoffAt is required"
        );
    }
}