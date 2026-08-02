package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;

import java.util.Objects;

public record FindLatestScientificFeatureVectorQuery(
        ParticipantId participantId,
        FeatureSetVersion featureSetVersion
) {

    public FindLatestScientificFeatureVectorQuery {
        Objects.requireNonNull(
                participantId,
                "participantId is required"
        );

        Objects.requireNonNull(
                featureSetVersion,
                "featureSetVersion is required"
        );
    }
}