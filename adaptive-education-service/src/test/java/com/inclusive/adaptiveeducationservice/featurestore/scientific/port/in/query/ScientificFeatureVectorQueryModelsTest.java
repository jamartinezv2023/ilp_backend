package com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ScientificFeatureVectorQueryModelsTest {

    private static final ParticipantId PARTICIPANT_ID =
            new ParticipantId(
                    "PARTICIPANT-QUERY-001"
            );

    private static final FeatureSetVersion FEATURE_SET_VERSION =
            new FeatureSetVersion(
                    "SCIENTIFIC-FEATURES-V1"
            );

    private static final Instant FEATURE_CUTOFF_AT =
            Instant.parse(
                    "2026-08-02T12:00:00Z"
            );

    @Test
    void shouldCreateExactQuery() {
        FindExactScientificFeatureVectorQuery query =
                new FindExactScientificFeatureVectorQuery(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION,
                        FEATURE_CUTOFF_AT
                );

        assertThat(query.participantId())
                .isEqualTo(PARTICIPANT_ID);

        assertThat(query.featureSetVersion())
                .isEqualTo(FEATURE_SET_VERSION);

        assertThat(query.featureCutoffAt())
                .isEqualTo(FEATURE_CUTOFF_AT);
    }

    @Test
    void shouldRejectExactQueryWithoutParticipant() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new FindExactScientificFeatureVectorQuery(
                                null,
                                FEATURE_SET_VERSION,
                                FEATURE_CUTOFF_AT
                        )
                )
                .withMessageContaining(
                        "participantId"
                );
    }

    @Test
    void shouldRejectExactQueryWithoutFeatureSetVersion() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new FindExactScientificFeatureVectorQuery(
                                PARTICIPANT_ID,
                                null,
                                FEATURE_CUTOFF_AT
                        )
                )
                .withMessageContaining(
                        "featureSetVersion"
                );
    }

    @Test
    void shouldRejectExactQueryWithoutFeatureCutoff() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new FindExactScientificFeatureVectorQuery(
                                PARTICIPANT_ID,
                                FEATURE_SET_VERSION,
                                null
                        )
                )
                .withMessageContaining(
                        "featureCutoffAt"
                );
    }

    @Test
    void shouldCreateLatestCompletedQuery() {
        FindLatestScientificFeatureVectorQuery query =
                new FindLatestScientificFeatureVectorQuery(
                        PARTICIPANT_ID,
                        FEATURE_SET_VERSION
                );

        assertThat(query.participantId())
                .isEqualTo(PARTICIPANT_ID);

        assertThat(query.featureSetVersion())
                .isEqualTo(FEATURE_SET_VERSION);
    }

    @Test
    void shouldRejectLatestQueryWithoutParticipant() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new FindLatestScientificFeatureVectorQuery(
                                null,
                                FEATURE_SET_VERSION
                        )
                )
                .withMessageContaining(
                        "participantId"
                );
    }

    @Test
    void shouldRejectLatestQueryWithoutFeatureSetVersion() {
        assertThatNullPointerException()
                .isThrownBy(() ->
                        new FindLatestScientificFeatureVectorQuery(
                                PARTICIPANT_ID,
                                null
                        )
                )
                .withMessageContaining(
                        "featureSetVersion"
                );
    }
}