package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureVector;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;

import java.time.Instant;
import java.util.Optional;

public interface ScientificFeatureVectorQueryPort {

    boolean exists(
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion,
            Instant featureCutoffAt
    );

    Optional<ScientificFeatureVector> findExact(
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion,
            Instant featureCutoffAt
    );

    Optional<ScientificFeatureVector> findLatestCompleted(
            ParticipantId participantId,
            FeatureSetVersion featureSetVersion
    );
}